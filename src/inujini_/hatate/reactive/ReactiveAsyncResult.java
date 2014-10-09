package inujini_.hatate.reactive;

import lombok.Getter;
import lombok.Setter;

/**
 * ReactiveAsyncTaskで実行した結果を保持するクラス
 * エラーが発生した場合はerrorに格納される
 * @param <TReturn>
 */
public class ReactiveAsyncResult<TReturn> {
	@Getter @Setter private TReturn result;
	@Getter @Setter private Exception error;

	public boolean hasError() { return this.error != null; }

}
