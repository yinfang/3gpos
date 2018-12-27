package com.clubank.util;

import com.clubank.domain.Result;

public interface PostAsyncTask {

	void afterWork(int id, Result result);
}
