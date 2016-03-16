package com.forkjoin.demo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * ========================================================
 * 日 期：2016年3月16日 上午11:13:36
 * 作 者：jiabin.qi
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class CountTask extends RecursiveTask<Integer> {
	
	private static final long serialVersionUID = 9173189225110485152L;
	private static final int threshold = 2;//阈值
	private int start;
	private int end;
	
	public CountTask(int start, int end) {
		super();
		this.start = start;
		this.end = end;
	}

	@Override
	protected Integer compute() {
		int sum = 0;
		boolean canCompute = (end - start) <= threshold;
		//如果任务足够小就计算任务
		if (canCompute) {
			for (int i = start; i <= end; i++) {
				sum += i;
			}
		}else {
			//如果任务大于阈值，就分裂成两个子任务计算
			int middle = (start + end) / 2;
			CountTask leftTask = new CountTask(start, middle);
			CountTask rightTask = new CountTask(middle + 1, end);
			//执行子任务
			leftTask.fork();
			rightTask.fork();
			//等待子任务完成，并得到结果
			int leftResult = leftTask.join();
			int rightResult = rightTask.join();
			//合并子任务
			sum = leftResult + rightResult;
		}
		return sum;
	}
	
	public static void main(String[] args) {
		try {
			ForkJoinPool forkJoinPool = new ForkJoinPool();
			//生成一个计算任务，负责计算1+2+3+4
			CountTask task = new CountTask(1, 4);
			//执行任务
			Future<Integer> result = forkJoinPool.submit(task);
			System.out.println(result.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}


