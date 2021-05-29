# Organisation

## Contributions
We organized with a system where the group would decide on a collective goal for the project, and we would work as necessary on it. Explicitly defined areas of responsibility would not be allocated. Instead group members would work on their own tasks and alert the group on what they were working on to prevent overlap. When a group member encountered a problem, they would bring it to the attention of the rest of the group, where it would either be handed off to another group member or the group would help troubleshoot the problem. Sometimes two or more people would work on the same problem simultaneously, with the first successful solution being pushed to the repository. In addition to Git, code fragments that were deemed too small to push were shared through Discord.

- **Reuben Catchpole (RealOlympusDev)** | Contributed to almost every part of the project. 
- **Drew Koke (DrewKoke)** | Created concert and performer classes, worked heavily on logins, cookies,  users and other miscellaneous tasks. Most responsible for subscriptions
- **Luke Windmeyer (Windy18)** | Worked heavily on bookings, seats and cookies. Also worked on database concurrency, exception handling and code formatting, amongst other tasks. Often worked in tandem via Discord with others to help them solve problems such as token authentication, mapping classes, and returning 403 statuses.

## Strategy for minimising the chance of concurrency errors
Versioning is enabled for tables where the possibility of concurrency errors exists. When an Exception is thrown, the transaction is rolled back to prevent the entry of incorrect data. 

## Domain model organisation
To get authorisation to post to the database, the authentication cookie is checked against a table consisting of authorisation tokens. Each booking is comprised of at least one Seat and one User, Seats are assigned to a concert through a common date but are not explicitly joined. Concerts have a many to many relationship with Performers, Subscriptions use the concertid as a foreign key, but not explicitly jointed. 