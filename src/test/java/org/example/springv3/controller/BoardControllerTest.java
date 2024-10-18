package org.example.springv3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springv3.board.BoardRequest;
import org.example.springv3.core.util.JwtUtil;
import org.example.springv3.user.User;
import org.example.springv3.user.UserRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

//spring 트랜잭션을 넣어준다. DataJpa? 테스트 할 때는 내부에 Transactional이 내부에 붙어 있었지만 SpringBootTest에는 없으므로 붙여 준다.
//테스트 코드에서는 트랜잭션을 붙이면 롤백 시켜준다. 그러면 회원가입과 로그인이 격리된다. 이때 로그인은 더미 계정으로 로그인 테스트 하는 것이다. 격리!
@Transactional
//아래 2 어노테이션은 항상 고정이다.
//Mock을 빈 컨테이너에 띄우는 것.
@AutoConfigureMockMvc
//MOCK 환경은 가짜 8080포트를 띄우는 것이다.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BoardControllerTest {


    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper om = new ObjectMapper();
    private String accessToken;

    //각각의 테스트 메서드가 실행 되기 전마다 실행해주는 어노테이션. 참고로 @BeforeAll은 전체 테스트 과정에서 맨 처음에 단 한 번만 실행하는 어노테이션.
    @BeforeEach
    public void setUp() {
        //given
        //글쓰기는 인증이 필요한데 우리는 JWT 토큰으로 인증을 하기 때문에 user를 하나 만들어서 토큰을 생성해서 생성한 토큰을 전달해주면 된다.
        //참고로 우리가 공부한다고 토큰을 생성하면 Bearer 떼고 전달했는데 사실 붙어서 전달하는 것이 테스트할 때도 편하다.
        System.out.println("나 실행돼?");
        User sessionUser = User.builder().id(1).username("ssar").build();
        accessToken = JwtUtil.create(sessionUser);
        System.out.println(accessToken);

    }


    @Test
    public void save_test() throws Exception {
        BoardRequest.SaveDTO saveDTO = new BoardRequest.SaveDTO();
        saveDTO.setTitle("title 11");
        saveDTO.setContent("content 11");


        //JoinDTO를 json으로 바꾸는 것. 즉 asString으로 읽는 것
        String requestBody = om.writeValueAsString(saveDTO);
        //System.out.println(requestBody);

        //when
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.post("/api/board")
                        .header("Authorization", "Bearer " +accessToken)
                .content(requestBody)
                //body 데이터 없으면 데이터 설명할 필요가 없다. 그래서 글삭제에서는 .content와 .contentType은 모두 삭제한다.
                .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBoyd = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBoyd);


        //when
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.id").value(11));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.title").value("title 11"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.content").value("content 11"));


    }


    @Test
    public void delete_test() throws Exception {

        int id = 2;



        //when
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.delete("/api/board/" +id)
                .header("Authorization", "Bearer " + accessToken)
                //body 데이터 없으면 데이터 설명할 필요가 없다. 그래서 글삭제에서는 .content와 .contentType은 모두 삭제한다.
        );

        //eye
        String responseBoyd = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBoyd);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body").isEmpty());


    }





}


