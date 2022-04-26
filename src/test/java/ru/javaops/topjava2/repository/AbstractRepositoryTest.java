package ru.javaops.topjava2.repository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
public abstract class AbstractRepositoryTest {
}
