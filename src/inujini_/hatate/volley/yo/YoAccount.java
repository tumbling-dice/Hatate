/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
 
package inujini_.hatate.volley.yo;

public class YoAccount {
	@Getter String newAccountUserName;
	@Getter String newAccountPasscode;
	@Getter String apiToken;
	@Getter @Setter String callbackUrl;
	@Getter @Setter String email;
	@Getter @Setter String description;
	@Getter @Setter boolean isNeedsLocation;
	
	public YoAccount(String newAccountUserName, String newAccountPasscode, String apiToken) {
		this.newAccountUserName = newAccountUserName;
		this.newAccountPasscode = newAccountPasscode;
		this.apiToken = apiToken;
	}
	
	public class Builder {
		private final YoAccount _accounts;
		
		public Builder(String newAccountUserName, String newAccountPasscode, String apiToken) {
			_accounts = new accounts(newAccountUserName, newAccountPasscode, apiToken);
		}
		
		public YoAccount build() {
			return _accounts;
		}
		
		public Builder callbackUrl(String callbackUrl) {
			_accounts.setCallbackUrl(callbackUrl);
			return this;
		}
		
		public AccountsBuilder email(String email) {
			_accounts.setEmail(email);
			return this;
		}
		
		public Builder description(String description) {
			_accounts.setDescription(description);
			return this;
		}
		
		public Builder needsLocation(boolean isNeedsLocation) {
			_accounts.isNeedsLocation(isNeedsLocation);
			return this;
		}
	}
}
