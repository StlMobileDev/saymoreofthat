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
		When I use session "A" to add event "Event" starting on "2008-09-02-1747"
		Then I should get a response with a status code of 303
		And the returned URI should point to an event named "Event" starting on "2008-09-02-1747" in time zone "America/Chicago"
		
	Scenario: User creates an event with an empty name
		Given a user in the database with session "A" and email "heath@borders.com"
		When I use session "A" to add event "" starting on "2008-09-02-1747"
		Then I should get a response with a status code of 400
		
	Scenario: User ends an event
		Given a user in the database with session "A" and email "heath@borders.com"
		And an event from session "A" named "Event" starting on "2008-09-02-1747"
		When I use session "A" to end event "Event"
		Then I should get a response with a status code of 204
		And the end date of event "Event" on session "A" should be within one minute of now
	
	Scenario: User ends an ended event
		Given a user in the database with session "A" and email "heath@borders.com"
		And an event from session "A" named "Event" starting on "2008-09-02-1747"
		And event "Event" from session "A" has ended
		When I use session "A" to end event "Event"
		Then I should get a response with a status code of 400
		
	Scenario: User votes
		Given a user in the database with session "A" and email "heath@borders.com"
		And an event from session "A" named "Event" starting on "2008-09-02-1747"
		When I cast a vote of value "5" from session "B" for event "Event" from session "A"
		Then event "Event" from session "A" should have a vote of value "5" with a time within one minute of now
		And I should get a response with a status code of 204
		And the JSON of event "Event" from session "A" should have the votes in an array
		
	Scenario: Two Users vote
		Given a user in the database with session "A" and email "heath@borders.com"
		And an event from session "A" named "Event" starting on "2008-09-02-1747"
		When I cast a vote of value "5" from session "B" for event "Event" from session "A"
		And I cast a vote of value "3" from session "C" for event "Event" from session "A"
		Then event "Event" from session "A" should have a vote of value "5" with a time within one minute of now
		And event "Event" from session "A" should have a vote of value "5" with a time within one minute of now
	
	Scenario: User votes for an ended event
		Given a user in the database with session "A" and email "heath@borders.com"
		And an event from session "A" named "Event" starting on "2008-09-02-1747"
		And event "Event" from session "A" has ended
		When I cast a vote of value "5" from session "B" for event "Event" from session "A"
		Then I should get a response with a status code of 400
