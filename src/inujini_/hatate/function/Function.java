/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.function;

public final class Function {
	private Function() {}

	public interface IAction {}

	public interface Action extends IAction {
		void call();
	}

	public interface Action1<T> extends IAction {
		void call(T x);
	}

	public interface Action2<T1, T2> extends IAction {
		void call(T1 x1, T2 x2);
	}

	public interface Action3<T1, T2, T3> extends IAction {
		void call(T1 x1, T2 x2, T3 x3);
	}

	public interface Action4<T1, T2, T3, T4> extends IAction {
		void call(T1 x1, T2 x2, T3 x3, T4 x4);
	}

	public interface Action5<T1, T2, T3, T4, T5> extends IAction {
		void call(T1 x1, T2 x2, T3 x3, T4 x4, T5 x5);
	}

	public interface Actions extends IAction {
		void call(Object... args);
	}

	public interface IFunc {}

	public interface Func<R> extends IFunc {
		R call();
	}

	public interface Func1<T, R> extends IFunc {
		R call(T x);
	}

	public interface Func2<T1, T2, R> extends IFunc {
		R call(T1 x1, T2 x2);
	}

	public interface Func3<T1, T2, T3, R> extends IFunc {
		R call(T1 x1, T2 x2, T3 x3);
	}

	public interface Func4<T1, T2, T3, T4, R> extends IFunc {
		R call(T1 x1, T2 x2, T3 x3, T4 x4);
	}

	public interface Func5<T1, T2, T3, T4, T5, R> extends IFunc {
		R call(T1 x1, T2 x2, T3 x3, T4 x4, T5 x5);
	}

	public interface Funcs<R> extends IFunc {
		R call(Object... args);
	}

	public interface Predicate<T> extends Func1<T, Boolean> {}

}