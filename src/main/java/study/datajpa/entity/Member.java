package study.datajpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of={"id","username","age"})
public class Member {

    // entity 는 디폴트 생성자가 있어야 함 protected 까지 열어놔야 함
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team!=null) changeTeam(team);
    }

    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }


}
