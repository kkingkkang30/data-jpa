package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public void delete(Member member){
        em.remove(member);
    }

    public List<Member> findAll(){

        //JPQL 테이블이 아니라 엔티티들 대상으로 함
        return em.createQuery("select m from Member m",Member.class).
                getResultList();
    }

    public Optional<Member> findById(Long id){
        return Optional.ofNullable(em.find(Member.class, id));
    }

    public long count(){
        return em.createQuery("select count(m) from Member m",Long.class)
                .getSingleResult();
    }
    public Member find(Long id){
        return em.find(Member.class, id);
    }


    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age){
            return em.createQuery("select m from Member m where m.username = :username and m.age > :age")
                    .setParameter("username", username)
                    .setParameter("age",age)
                    .getResultList();
    }

    // paging query
    public List<Member> findByPage(int age, int offset, int limit){
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long totalCount(int age){
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    public int bulkAgePlus(int age){
        return em.createQuery("update Member m set m.age = m.age+1 where m.age>=:age")
                .setParameter("age", age)
                .executeUpdate();
    }
}
