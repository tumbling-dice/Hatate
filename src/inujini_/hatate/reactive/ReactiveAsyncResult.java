/**
 * HatateHoutyouAlarm
 * 
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.reactive;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>ReactiveAsyncTaskで実行した結果を保持するクラス</p>
 * <p>エラーが発生した場合はerrorに格納される</p>
 * @param <TReturn>
 */
public class ReactiveAsyncResult<TReturn> {
	@Getter @Setter private TReturn result;
	@Getter @Setter private Exception error;

	public boolean hasError() { return this.error != null; }

}
