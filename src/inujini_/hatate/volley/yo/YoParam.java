/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.volley.yo;

import java.io.Serializable;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * <p>//api.justyo.co/yo/および//api.justyo.co/yoall/で使用するparamのWrapper.</p>
 * <p>yoの方はapiTokenだけでなくuserNameも必須となるので注意.</p>
 * @see YoAPI
 * @see YoParam.Builder
 * @see YoRequest
 */
public class YoParam implements Serializable {
	private static final long serialVersionUID = -8290811847682126819L;

	@Getter private final String apiToken;

	/** Yoを送りつけるユーザ名 */
	@Getter @Setter private String userName;
	/** Yoに含むリンク */
	@Getter @Setter private String link;
	/** 緯度 */
	@Getter private Double latitude;
	/** 経度 */
	@Getter private Double longitude;

	/**
	 * <p>//api.justyo.co/yo/および//api.justyo.co/yoall/で使用するparamのWrapper.</p>
	 * <p>yoの方はapiTokenだけでなくuserNameも必須となるので注意.</p>
	 * @param apiToken api_token
	 * @see YoAPI
	 * @see YoParam.Builder
	 * @see YoRequest
	 */
	public YoParam(@NonNull String apiToken) {
		this.apiToken = apiToken;
	}

	/**
	* //api.justyo.co/yo/で使用するparamのWrapper.
	* @param apiToken api_token
	* @param userName Yoを送りつけるユーザ名
	* @see YoAPI
	* @see YoParam.Builder
	* @see YoRequest
	*/
	public YoParam(@NonNull String apiToken, @NonNull String userName) {
		this.apiToken = apiToken;
		this.userName = userName;
	}

	/**
	 * 位置情報設定.
	 * @param latitude 緯度
	 * @param longitude 経度
	 */
	public void setLocation(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * 位置情報有無.
	 * @return 緯度と経度に何らかの値が設定されていればtrue
	 */
	public boolean hasLocation() {
		return this.latitude != null && this.longitude != null;
	}

	/**
	* <p>//api.justyo.co/yo/および//api.justyo.co/yoall/で使用するparamのBuilder.</p>
	* <p>yoの方はapiTokenだけでなくuserNameも必須となるので注意.</p>
	*/
	public static class Builder {
		private final YoParam _param;

		/**
		 * <p>//api.justyo.co/yo/および//api.justyo.co/yoall/で使用するparamのBuilder.</p>
		 * <p>yoの方はapiTokenだけでなくuserNameも必須となるので注意.</p>
		 * @param apiToken api_token
		 */
		public Builder(@NonNull String apiToken) {
			_param = new YoParam(apiToken);
		}

		/**
		 * //api.justyo.co/yo/および//api.justyo.co/yoall/で使用するparamのBuilder.
		 * @param apiToken api_token
		 * @param userName Yoを送りつけるユーザ名
		 */
		public Builder(@NonNull String apiToken, @NonNull String userName) {
			_param = new YoParam(apiToken, userName);
		}

		/**
		 *
		 * @return YoParam
		 */
		public YoParam build() {
			return _param;
		}

		/**
		 * Yoを送りつけるユーザ名.
		 * @param userName
		 * @return
		 */
		public Builder userName(String userName) {
			_param.setUserName(userName);
			return this;
		}

		/**
		 * Yoに含むリンク.
		 * @param link
		 * @return
		 */
		public Builder link(String link) {
			_param.setLink(link);
			return this;
		}

		/**
		 * 位置情報.
		 * @param latitude 緯度
		 * @param longitude 経度
		 * @return
		 */
		public Builder location(double latitude, double longitude) {
			_param.setLocation(latitude, longitude);
			return this;
		}
	}
}
