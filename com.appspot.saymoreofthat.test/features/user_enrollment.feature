Feature: User Enrollment
	So that we can uniquely identify users
	They should be able to enroll with a email address
	And get a session that persists for ever and ever and ever
	
	Scenario: New user hitting enrollment with a new email address
		When I hit the enrollment form with email "heath@borders.com" and session "A"
		Then I should get a response with a status code of 204
		And session "A" should be associated with email "heath@borders.com"
	
	Scenario: New user hitting enrollment without an email address
		When I hit the enrollment form with email "" and session "A"
		Then I should get a response with a status code of 400
		And there should be no users in the database
	
	Scenario: Existing client with session re-registers
		Given a user in the database with session "A" and email "heath@borders.com"
		When I hit the enrollment form with email "heath@borders.com" and session "A"
		Then I should get a response with a status code of 204
		And session "A" should be associated with email "heath@borders.com"
		
	Scenario: Existing client with multiple sessions re-registers
		Given a user in the database with session "A" and email "heath@borders.com"
		And a user in the database with session "B" and email "heath@borders.com"
		When I hit the enrollment form with email "heath@borders.com" and session "A"
		Then I should get a response with a status code of 204
		And session "A" should be associated with email "heath@borders.com"
		And session "B" should be associated with email "heath@borders.com"
	
	Scenario: User registering for an already existing email
		Given a user in the database with session "A" and email "heath@borders.com"
		When I hit the enrollment form with email "heath@borders.com" and session "B"
		Then I should get a response with a status code of 202
		And session "A" should be associated with email "heath@borders.com"
		And an email should be sent to "heath@borders.com" with session "B"
		
	Scenario: User confirming a session given by email
		Given a user in the database with session "A" and email "heath@borders.com"
		And a requested session "B" in the database for email "heath@borders.com"
		When I confirm session "B"
		Then I should get a response with a status code of 204
		And session "A" should be associated with email "heath@borders.com"
		And session "B" should be associated with email "heath@borders.com"
		And no requested sessions should exist in the database for email "heath@borders.com"
	
	Scenario: User denying a session given by email
		Given a user in the database with session "A" and email "heath@borders.com"
		And a requested session "B" in the database for email "heath@borders.com"
		When I deny session "B"
		Then I should get a response with a status code of 204
		And session "A" should be associated with email "heath@borders.com"
		And session "B" should not be associated with email "heath@borders.com"
		And no requested sessions should exist in the database for email "heath@borders.com"
		
	Scenario: User creates an event
		Given a user in the database with session "A" and email "heath@borders.com"
		When I use session "A" to add event "Event" starting on "2008-09-02-1747" in time zone "America/Chicago"
		Then I should get a response with a status code of 303
		And the returned URI should point to an event named "Event" starting on "2008-09-02-1747" in time zone "America/Chicago"
		
	Scenario: User ends an event
		Given a user in the database with session "A" and email "heath@borders.com"
		And an event named "Event" starting on "2008-09-02-1747" in time zone "America/Chicago"
		When I use session "A" to end the event
		Then I should get a response with a status code of 204
		And the end date of the even should be non-zero