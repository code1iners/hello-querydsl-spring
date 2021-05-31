package hello.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.querydsl.entity.Member;
import hello.querydsl.entity.QMember;
import hello.querydsl.entity.Team;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired EntityManager em;
    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

        Team team1 = new Team("team1");
        Team team2 = new Team("team2");
        em.persist(team1);
        em.persist(team2);

        Member member1 = new Member("member1", 10, team1);
        Member member2 = new Member("member2", 20, team1);
        Member member3 = new Member("member3", 30, team2);
        Member member4 = new Member("member4", 40, team2);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    /**
     * <h3>Jpql.</h3>
     */
    @Test
    public void jpql() throws Exception {
        // given

        // when
        Member foundMemberByJpql = em.createQuery("select m From Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        // then
        assertThat(foundMemberByJpql.getUsername()).isEqualTo("member1");
    }

    /**
     * <h3>Querydsl.</h3>
     */
    @Test
    public void querydsl() throws Exception {
        // given
        QMember m = QMember.member;
        m = new QMember("m"); // note. Same way above.

        // when
        Member foundMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1"))
                .fetchOne();

        // then
        assertThat(foundMember.getUsername()).isEqualTo("member1");
    }

}
