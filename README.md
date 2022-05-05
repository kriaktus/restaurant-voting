## Restaurant voting [![Codacy Badge](https://app.codacy.com/project/badge/Grade/18a5fa642d6048a3a77abc8df26ddc42)](https://www.codacy.com/gh/kriaktus/topjava2/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=kriaktus/topjava2&amp;utm_campaign=Badge_Grade)

### Graduation project from course [TopJava](https://javaops.ru/view/topjava)


> Design and implement a REST API using Hibernate/Spring/SpringMVC (or Spring-Boot) without frontend.
> 
>The task is:
>
>Build a voting system for deciding where to have lunch.
>- 2 types of users: admin and regular users
>- Admin can input a restaurant and it's lunch menu of the day (2-5 items usually, just a dish name and price)
>- Menu changes each day (admins do the updates)
>- Users can vote on which restaurant they want to have lunch at
>- Only one vote counted per user
>- If user votes again the same day:
>  - If it is before 11:00 we assume that he changed his mind. 
>  - If it is after 11:00 then it is too late, vote can't be changed
>- Each restaurant provides a new menu each day.


project based on open course [Spring Boot 2.x + HATEOAS](https://javaops.ru/view/bootjava) project (without dishes)

-------------------------------------------------------------
- Stack: [JDK 17](http://jdk.java.net/17/), Spring Boot 2.5, Lombok, H2, Caffeine Cache, Swagger/OpenAPI 3.0
- Run: `mvn spring-boot:run` in root directory.
-----------------------------------------------------
[REST API documentation](http://localhost:8080/swagger-ui.html)  
Credentials:
```
User:  user@yandex.ru  / password
Admin: admin@gmail.com / admin
User2: user2@yandex.ru / 328328
User3: user3@yandex.ru / 328328
```