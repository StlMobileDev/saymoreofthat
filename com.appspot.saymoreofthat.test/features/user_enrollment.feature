Feature: User Enrollment
	So that we can uniquely identify users
	They should be able to enroll with a email address
	And get a session that persists for ever and ever and ever
	
	@wip
	Scenario: New user hitting enrollment with a new email address
		Given a client without a session
		When I hit the enrollment form with "heath@borders.com"
		Then I should get a response with a status code of 200
		And there should be 1 session in the database for email "heath@borders.com"
	
	@wip
	Scenario: New user hitting enrollment without an email address
		Given a client without a session
		When I hit the enrollment form with ""
		Then I should get a 403
		And I should be redirected to "/blah"
		And there should be no sessions in the database
	
	@wip
	Scenario: Existing client with session re-registers should get 500
		Given a user in the database with session "session" and email "heath@borders.com"
		And a client with the session "sessionid"
		When I hit the enrollment form with "heath@borders.com"
		Then I should get a 500
		And there should be 1 session in the database
	
	@wip
	Scenario: New user registering for an already existing email
		Given a session in the database with sesisonid "sessionid" and email "heath@borders.com"
		Given a client without a session
		When I hit the enrollment form with "heath@borders.com"
		Then I should get a 500
		And there should be 1 session in the database