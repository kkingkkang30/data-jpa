package study.datajpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Member {

    // entity 는 디폴트 생성자가 있어야 함 protected 까지 열어놔야 함
    @Id
    @GeneratedValue
    private Long id;

    private String username;

    protected Member(){}

    public Member(String username) {
        this.username = username;
    }

}
