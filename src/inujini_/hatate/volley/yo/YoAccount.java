/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.volley.yo;

/**
 * //api.justyo.co/accounts/で使用するparamのWrapper.
 * @see YoAPI
 * @see YoAccount.Builder
 * @see YoRequest
 */
public class YoAccount implements Serializable {

    @Getter private final String newAccountUserName;
	@Getter private final String newAccountPasscode;
	@Getter private final String apiToken;

    /** YoをSendされた時に呼び出されるコールバックURL */
	@Getter @Setter private String callbackUrl;
    /** Emailアドレス */
	@Getter @Setter private String email;
    /** アカウントの説明 */
	@Getter @Setter private String description;
    /** 位置情報の使用有無 */
	@Getter @Setter private boolean isNeedsLocation;

    /**
     * //api.justyo.co/accounts/で使用するparamのWrapper.
     * @param newAccountUserName 新しいアカウントのユーザ名
     * @param newAccountPasscode 新しいアカウントのパスワード
     * @param apiToken api_token
     * @see YoAPI
     * @see YoAccount.Builder
     * @see YoRequest
     */
	public YoAccount(@NonNull String newAccountUserName, @NonNull String newAccountPasscode, @NonNull String apiToken) {
		this.newAccountUserName = newAccountUserName;
		this.newAccountPasscode = newAccountPasscode;
		this.apiToken = apiToken;
	}

    /**
     * //api.justyo.co/accounts/で使用するparamのBuilder.
     */
	public class Builder {
		private final YoAccount _accounts;

        /**
         * //api.justyo.co/accounts/で使用するparamのBuilder.
         * @param newAccountUserName 新しいアカウントのユーザ名
         * @param newAccountPasscode 新しいアカウントのパスワード
         * @param apiToken api_token
         */
		public Builder(@NonNull String newAccountUserName, @NonNull String newAccountPasscode, @NonNull String apiToken) {
			_accounts = new accounts(newAccountUserName, newAccountPasscode, apiToken);
		}

        /**
         *
         * @return YoAccount
         */
		public YoAccount build() {
			return _accounts;
		}

        /**
         * YoをSendされた時に呼び出されるコールバックURL.
         * @param callbackUrl
         * @return
         */
		public Builder callbackUrl(String callbackUrl) {
			_accounts.setCallbackUrl(callbackUrl);
			return this;
		}

        /**
         * Emailアドレス
         * @param email
         * @return
         */
		public AccountsBuilder email(String email) {
			_accounts.setEmail(email);
			return this;
		}

        /**
         * アカウントの説明.
         * @param description
         * @return
         */
		public Builder description(String description) {
			_accounts.setDescription(description);
			return this;
		}

        /**
         * 位置情報の使用有無.
         * @param isNeedsLocation
         * @return
         */
		public Builder needsLocation(boolean isNeedsLocation) {
			_accounts.isNeedsLocation(isNeedsLocation);
			return this;
		}
	}
}
