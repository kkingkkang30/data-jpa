package study.datajpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {
    @PersistenceContext
    EntityManager em;

    @Test
    public void testEntity(){
        var teamA = new Team("teamA");
        var teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        var member1 = new Member("member1",10,teamA);
        var member2 = new Member("member2",13,teamB);
        var member3 = new Member("member3",16,teamA);
        var member4 = new Member("member4",20,teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        // 강제로 db insert 날려버려
        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for(Member member: members){
            System.out.println("member =" +member);
            System.out.println("> member.team= " + member.getTeam());
        }
    }


}