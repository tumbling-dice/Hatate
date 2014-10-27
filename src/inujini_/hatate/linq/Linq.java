/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.linq;

import inujini_.hatate.function.Function.Action1;
import inujini_.hatate.function.Function.Func1;
import inujini_.hatate.function.Function.Func2;
import inujini_.hatate.function.Function.Predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Linq<T> implements Iterable<T> {

	private Iterable<T> _items;

	private Linq(Iterable<T> items) {
		_items = items;
	}

	private Linq(T[] array) {
		_items = Arrays.asList(array);
	}

	@SuppressWarnings("unchecked")
	private Linq(byte[] array) {
		Byte[] a = new Byte[array.length];
		for (int i = 0; i < array.length; i++)
			a[i] = array[i];

		_items = (Iterable<T>) Arrays.asList(a);
	}

	@SuppressWarnings("unchecked")
	private Linq(int[] array) {
		Integer[] a = new Integer[array.length];
		for (int i = 0; i < array.length; i++)
			a[i] = array[i];
		_items = (Iterable<T>) Arrays.asList(a);
	}

	@SuppressWarnings("unchecked")
	private Linq(short[] array) {
		Short[] a = new Short[array.length];
		for (int i = 0; i < array.length; i++)
			a[i] = array[i];
		_items = (Iterable<T>) Arrays.asList(a);
	}

	@SuppressWarnings("unchecked")
	private Linq(long[] array) {
		Long[] a = new Long[array.length];
		for (int i = 0; i < array.length; i++)
			a[i] = array[i];
		_items = (Iterable<T>) Arrays.asList(a);
	}

	@SuppressWarnings("unchecked")
	private Linq(float[] array) {
		Float[] a = new Float[array.length];
		for (int i = 0; i < array.length; i++)
			a[i] = array[i];
		_items = (Iterable<T>) Arrays.asList(a);
	}

	@SuppressWarnings("unchecked")
	private Linq(double[] array) {
		Double[] a = new Double[array.length];
		for (int i = 0; i < array.length; i++)
			a[i] = array[i];
		_items = (Iterable<T>) Arrays.asList(a);
	}

	@SuppressWarnings("unchecked")
	private Linq(char[] array) {
		Character[] a = new Character[array.length];
		for (int i = 0; i < array.length; i++)
			a[i] = array[i];
		_items = (Iterable<T>) Arrays.asList(a);
	}

	@SuppressWarnings("unchecked")
	private Linq(boolean[] array) {
		Boolean[] a = new Boolean[array.length];
		for (int i = 0; i < array.length; i++)
			a[i] = array[i];
		_items = (Iterable<T>) Arrays.asList(a);
	}

	public static <T> Linq<T> linq(Iterable<T> items) {
		return new Linq<T>(items);
	}

	public static <T> Linq<T> linq(T[] array) {
		return new Linq<T>(array);
	}

	public static Linq<Byte> linq(byte[] array) {
		return new Linq<Byte>(array);
	}

	public static Linq<Integer> linq(int[] array) {
		return new Linq<Integer>(array);
	}

	public static Linq<Short> linq(short[] array) {
		return new Linq<Short>(array);
	}

	public static Linq<Long> linq(long[] array) {
		return new Linq<Long>(array);
	}

	public static Linq<Float> linq(float[] array) {
		return new Linq<Float>(array);
	}

	public static Linq<Double> linq(double[] array) {
		return new Linq<Double>(array);
	}

	public static Linq<Character> linq(char[] array) {
		return new Linq<Character>(array);
	}

	public static Linq<Boolean> linq(boolean[] array) {
		return new Linq<Boolean>(array);
	}

	public Linq<T> where(Predicate<T> p) {

		ArrayList<T> tmp = new ArrayList<T>();

		for(T obj : _items) {
			if(p.call(obj)) {
				tmp.add(obj);
			}
		}

		_items = tmp;

		return this;
	}

	public <U> Linq<U> select(Func1<T, U> f) {

		ArrayList<U> tmp = new ArrayList<U>();

		for(T obj : _items) {
			tmp.add(f.call(obj));
		}

		return new Linq<U>(tmp);
	}

	public <U> Linq<U> selectMany(Func1<T, Iterable<U>> f) {

		ArrayList<U> tmp = new ArrayList<U>();

		for(T obj : _items) {
			for(U converted : f.call(obj)) {
				tmp.add(converted);
			}
		}

		return new Linq<U>(tmp);
	}

	public <U> Linq<U> selectManyArray(Func1<T, U[]> f) {

		ArrayList<U> tmp = new ArrayList<U>();

		for(T obj : _items) {
			for(U converted : f.call(obj)) {
				tmp.add(converted);
			}
		}

		return new Linq<U>(tmp);
	}

	public Linq<T> skip(int count) {

		if(count <= 0) return this;

		ArrayList<T> tmp = new ArrayList<T>();
		int i = 1;

		for(T obj : _items) {
			if(i > count) {
				tmp.add(obj);
			} else {
				i++;
			}
		}

		_items = tmp;

		return this;
	}

	public Linq<T> skipWhile(Predicate<T> p) {

		ArrayList<T> tmp = new ArrayList<T>();
		boolean isSkiped = false;

		for(T obj : _items) {
			if(!isSkiped) {
				isSkiped = p.call(obj);
			} else {
				tmp.add(obj);
			}
		}

		_items = tmp;
		return this;
	}

	public Linq<T> take(int count) {
		ArrayList<T> tmp = new ArrayList<T>();

		if(count <= 0) {
			_items = tmp;
			return this;
		}

		int i = 1;

		for(T obj : _items) {
			if(i < count) {
				tmp.add(obj);
			} else {
				i++;
			}
		}

		_items = tmp;
		return this;
	}

	public Linq<T> takeWhile(Predicate<T> p) {
		ArrayList<T> tmp = new ArrayList<T>();

		for(T obj : _items) {
			if(p.call(obj)){
				tmp.add(obj);
			} else {
				break;
			}
		}

		_items = tmp;

		return this;
	}

	public <V, U, W> Linq<W> join(Collection<U> rightItems, Func1<T, V> leftKeyProvider, Func1<U, V> rightKeyProvider, Func2<T, U, W> joinProvider) {

		ArrayList<W> tmp = new ArrayList<W>();
		ArrayList<W> cache = new ArrayList<W>();
		boolean isImplementedRemoveAll = true;
		boolean isTestedRemoveAll = false;

		for(T left : _items) {

			V leftKey = leftKeyProvider.call(left);

			for(U right : rightItems) {

				V rightKey = rightKeyProvider.call(right);

				if(leftKey.equals(rightKey)) {
					W obj = joinProvider.call(left, right);
					tmp.add(obj);
					if(isImplementedRemoveAll) cache.add(obj);
				}
			}

			if(!cache.isEmpty()) {
				if(!isTestedRemoveAll) {
					try {
						rightItems.removeAll(cache);
					} catch(UnsupportedOperationException e) {
						isImplementedRemoveAll = false;
					}

					isTestedRemoveAll = true;
				} else if(isImplementedRemoveAll) {
					rightItems.removeAll(cache);
				}

				cache.clear();
			}
		}

		return new Linq<W>(tmp);
	}

	public Linq<T> orderBy(Comparator<T> comparator) {
		List<T> list = toList();
		Collections.sort(list, comparator);
		_items = list;
		return this;
	}

	public boolean any() {
		return _items.iterator().hasNext();
	}

	public boolean any(Func1<T, Boolean> p) {
		for (T obj : _items) {
			if(p.call(obj)) {
				return true;
			}
		}
		return false;
	}

	public T first() {
		return _items.iterator().next();
	}

	public <TKey, TValue> Map<TKey, List<TValue>> groupBy(Func1<T, TKey> keyProvider, Func1<T, TValue> valueProvider) {
		Map<TKey, List<TValue>> map = new HashMap<TKey, List<TValue>>();

		for(T obj : _items) {

			TKey key = keyProvider.call(obj);

			if(map.containsKey(key)) {
				map.get(key).add(valueProvider.call(obj));
			} else {
				List<TValue> value = new ArrayList<TValue>();
				value.add(valueProvider.call(obj));
				map.put(key, value);
			}
		}

		return map;
	}

	public List<T> toList() {
		if(_items instanceof List<?>) return (List<T>) _items;

		List<T> list = new ArrayList<T>();

		for(T obj : _items) {
			list.add(obj);
		}

		return list;
	}

	public void forEach(Action1<T> action) {
		for (T obj : _items) {
			action.call(obj);
		}
	}

	public <TKey, TValue> Map<TKey, TValue> toMap(Func1<T, TKey> keyProvider, Func1<T, TValue> valueProvider) {
		Map<TKey, TValue> map = new HashMap<TKey, TValue>();

		for(T obj : _items) {
			TKey key = keyProvider.call(obj);
			if(key == null) continue;

			TValue value = valueProvider.call(obj);
			if(value == null) continue;

			map.put(key, value);
		}

		return map;
	}

	public Linq<T> distinct() {
		ArrayList<T> list = new ArrayList<T>();

		for (T obj : _items) {
			if(!list.contains(obj)) list.add(obj);
		}

		return new Linq<T>(list);
	}

	@SuppressWarnings("unchecked")
	public T[] toArray() {
		return (T[]) toList().toArray();
	}

	public static int[] toIntArray(Linq<Integer> linq) {
		List<Integer> base = linq.toList();
		int size = base.size();
		int[] arr = new int[size];

		for (int i = 0; i < size; i++) {
			arr[i] = base.get(i);
		}

		return arr;
	}

	public static long[] toLongArray(Linq<Long> linq) {
		List<Long> base = linq.toList();
		int size = base.size();
		long[] arr = new long[size];

		for (int i = 0; i < size; i++) {
			arr[i] = base.get(i);
		}

		return arr;
	}

	public static float[] toFloatArray(Linq<Float> linq) {
		List<Float> base = linq.toList();
		int size = base.size();
		float[] arr = new float[size];

		for (int i = 0; i < size; i++) {
			arr[i] = base.get(i);
		}

		return arr;
	}

	public static double[] toDoubleArray(Linq<Double> linq) {
		List<Double> base = linq.toList();
		int size = base.size();
		double[] arr = new double[size];

		for (int i = 0; i < size; i++) {
			arr[i] = base.get(i);
		}

		return arr;
	}

	public static short[] toShortArray(Linq<Short> linq) {
		List<Short> base = linq.toList();
		int size = base.size();
		short[] arr = new short[size];

		for (int i = 0; i < size; i++) {
			arr[i] = base.get(i);
		}

		return arr;
	}

	public static byte[] toByteArray(Linq<Byte> linq) {
		List<Byte> base = linq.toList();
		int size = base.size();
		byte[] arr = new byte[size];

		for (int i = 0; i < size; i++) {
			arr[i] = base.get(i);
		}

		return arr;
	}

	public static String toJoinedString(Linq<String> linq, String delimiter) {
		StringBuilder sb = new StringBuilder();
		for (String item : linq) {
			sb.append(item).append(delimiter);
		}

		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}

	@Override
	public Iterator<T> iterator() {
		return _items.iterator();
	}

}
