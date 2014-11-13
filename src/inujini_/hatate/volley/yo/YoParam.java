/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.volley.yo;

public class YoParam {
	@Getter String apiToken;
	@Getter @Setter String userName;
	@Getter @Setter String link;
	@Getter Double latitude;
	@Getter Double longitude;

	public YoParam(String apiToken) {
		this.apiToken = apiToken;
	}

	public void setLocation(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public boolean hasLocation() {
		return this.latitude != null && this.longitude != null;
	}

	public class Builder {
		private YoParam _param;

		public Builder(String apiToken) {
			_param = new YoParam(apiToken);
		}

		public YoParam build() {
			return _param;
		}

		public Builder userName(String userName) {
			_param.setUserName(userName);
			return this;
		}

		public Builder link(String link) {
			_param.setLink(link);
			return this;
		}

		public Builder location(double latitude, double longitude) {
			_param.setLocation(latitude, longitude);
			return this;
		}
	}
}