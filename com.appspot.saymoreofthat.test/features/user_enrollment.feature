Feature: User Enrollment
	So that we can uniquely identify users
	They should be able to enroll with a email address
	And get a session that persists for ever and ever and ever
	
	Scenario: New user hitting enrollment with a new email address
		Given a client without a session
		When I hit the enrollment form with email "heath@borders.com" and session "A"
		Then I should get a response with a status code of 200
		And session "A" should be associated with email "heath@borders.com"
	
	Scenario: New user hitting enrollment without an email address
		Given a client without a session
		When I hit the enrollment form with email "" and session "A"
		Then I should get a response with a status code of 500
		And there should be no users in the database
	
	Scenario: Existing client with session re-registers should get 500
		Given a user in the database with session "A" and email "heath@borders.com"
		And a client with session "A"
		When I hit the enrollment form with email "heath@borders.com" and session "A"
		Then I should get a response with a status code of 500
		And session "A" should be associated with email "heath@borders.com"
	
	@wip
	Scenario: New user registering for an already existing email
		Given a user in the database with session "A" and email "heath@borders.com"
		Given a client without a session
		When I hit the enrollment form with email "heath@borders.com" and session "B"
		Then I should get a response with a status code of 500
		And session "A" should be associated with email "heath@borders.com"