package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
}