package com.poly.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailContactRealEstate {
	String from;
	String to;
	String[] cc;
	String[] bcc;
	String subject;
	String body;
	String[] attachments;
	String fullNameTo;
	String fullNameFrom;
	String phone;

	public MailContactRealEstate(String to, String subject, String body, String fullNameTo) {
		this.from = "Real Estate <fab5@gmail.com.vn>";
		this.to = to;
		this.subject = subject;
		this.body = body;
		this.fullNameTo = fullNameTo;
	}
}
