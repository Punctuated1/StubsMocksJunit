package myapp;

public class EmailPackage {
	private String emailSender;
	private String emailSubject;
	private String emailDsitributionList;
	private String emailBody;

	public EmailPackage() {
	}

	public String getEmailSender() {
		return emailSender;
	}

	public void setEmailSender(String emailSender) {
		this.emailSender = emailSender;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailDsitributionList() {
		return emailDsitributionList;
	}

	public void setEmailDsitributionList(String emailDsitributionList) {
		this.emailDsitributionList = emailDsitributionList;
	}

	public String getEmailBody() {
		return emailBody;
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

}
