package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember(){

        var member = new Member("memberA");

        var savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);

    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan(){
        Member m1 = new Member("aaa",10);
        Member m2 = new Member("aaa",20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("aaa",15);

        assertThat(result.get(0).getUsername()).isEqualTo("aaa");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testQuery(){
        Member m1 = new Member("aaa",10);
        Member m2 = new Member("bbb",20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("aaa",10);
        assertThat(result.get(0).getAge()).isEqualTo(10);
    }

    @Test
    public void testUsername(){
        Member m1 = new Member("aaa",10);
        Member m2 = new Member("bbb",20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findUsername("aaa");
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testMemberDto(){

        Team team = new Team("team1");
        teamRepository.save(team);

        Member m1 = new Member("aaa",10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> result = memberRepository.findMemberDto();
        assertThat(result.get(0).getTeamname()).isEqualTo("team1");
    }
    @Test
    public void testUsernames(){
        Member m1 = new Member("aaa",10);
        Member m2 = new Member("bbb",20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("aaa","bbb"));
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testReturnType(){
        Member m1 = new Member("aaa",10);
        Member m2 = new Member("bbb",20);
        Member m3 = new Member("bbb",20);

        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);

        List<Member> findMembers = memberRepository.findListByUsername("aaab");
        Member findMember = memberRepository.findMemberByUsername("aaac");
        Optional<Member> findOptionalMember = memberRepository.findOptionalByUsername("aaad");

        // if member not exist
        assertThat(findMembers.size()).isEqualTo(0);
        assertThat(findMember).isNull();

        // if member is more than 2
        // findMember / findOptionalMember exception
        Member findMember2 = memberRepository.findMemberByUsername("bbb");
        Optional<Member> findOptionalMember2 = memberRepository.findOptionalByUsername("bbb");

    }

    @Test
    public void paging(){
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));

        PageRequest pageRequest= PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        int age = 10;

        // when
        Page<Member> members = memberRepository.findByAge(age,pageRequest);

        // page <member> 그대로 front에 보내면 절대 안됨 (엔티티 노출)
        Page<MemberDto> toMap = members.map(member -> new MemberDto(member.getId(), member.getUsername(), null));


        assertThat(members.getContent().size()).isEqualTo(3);
        assertThat(members.getTotalElements()).isEqualTo(5);
        assertThat(members.getNumber()).isEqualTo(0);  // page number
        assertThat(members.getTotalPages()).isEqualTo(2); // total page number
        assertThat(members.isFirst()).isTrue();
        assertThat(members.hasNext()).isTrue();

        Slice<Member> memberSlice = memberRepository.findSliceByAge(age,pageRequest);

        assertThat(memberSlice.getContent().size()).isEqualTo(3);
        assertThat(memberSlice.getNumber()).isEqualTo(0);  // page number
        assertThat(memberSlice.isFirst()).isTrue();
        assertThat(memberSlice.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate(){
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",13));
        memberRepository.save(new Member("member3",20));
        memberRepository.save(new Member("member4",26));
        memberRepository.save(new Member("member5",40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);

        // bulk 연산에서 조심해야함..  bulk 연산은 영속성 컨텍스트 없이 바로 db 업데이트 때려버려서 영속성 컨텍스트가 모름
        // 그래서 영속성 컨텍스트를 날려버려야 40->50이 반영됨을 알 수 있음
        Member resultFlushBefore = memberRepository.findMemberByUsername("member5");
        assertThat(resultFlushBefore.getAge()).isEqualTo(40);

        em.flush(); // db 에 반영
        em.clear();
        // clear 안하려면   @Modifying(clearAutomatically = true) 설정해주면 됨

        Member result = memberRepository.findMemberByUsername("member5");

        //then
        assertThat(resultCount).isEqualTo(3);
        assertThat(result.getAge()).isEqualTo(50);
    }

    @Test
    public void findMemberLazy(){
        //given
        // member1 -> teamA
        // member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll();

        List<Member> fetchMembers = memberRepository.findMemberFetchJoin();

        for(Member member : members){
            System.out.println("member = " + member.getUsername());
            // proxy 가짜 객체 (lazy fetch)
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }

        // fetch 한번에 다 끌고옴
        for(Member member : fetchMembers){
            System.out.println("member = " + member.getUsername());
            // 진짜 객체
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void findEntityFetch() {
        //given
        // member1 -> teamA
        // member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll();


        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            // proxy 가짜 객체 (lazy fetch)
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }

    }

    @Test
    public void queryHint(){
        // given
        Member saved = memberRepository.save(new Member("member1", 10));

        em.flush();
        em.clear();

        // when

        // 조회용으로만 쓸 용도일 때 hint 주기
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        // member2 로 update를 안침
        findMember.setUsername("member2");

        em.flush();

        // select for update 문이 실행됨.
        List<Member> findMem = memberRepository.findLockByUsername("member1");

    }
}