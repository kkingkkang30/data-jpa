package study.datajpa.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3By();

    // 컴파일 할 때 sql 오타 잡아줌
    // 동적 쿼리는 querydsl 해야됨
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username,@Param("age") int age);

    // jpql 로 string
    @Query("select m.username from Member m")
    List<String> findUsername(@Param("username") String username);

    // dto 로 리턴
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // 물론 jparepo 로 가능함
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUsername(String username); // collection
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 optional

    // count query는 조인 할 필요가 없으니 따로 분리할 수 있음
    @Query(value="select m from Member  m left join m.team t", countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    Slice<Member> findSliceByAge(int age, Pageable pageable);

    // modifying 어노테이션 있어야 jparepo 의 executeUpdate 실행함 .
    @Modifying //(clearAutomatically = true)
    @Query("update Member m set m.age = m.age +10 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member  m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // --- 전부 다 fetch 조인 되게 함 --- //
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @Query("select m from Member m")
    @EntityGraph(attributePaths = {"team"})
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    // -------- //

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    // select for update
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
