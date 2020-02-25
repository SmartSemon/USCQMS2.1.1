package com.usc.app.wxdd;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccessToken
{
	private String token;
	private int expiresIn;
}
