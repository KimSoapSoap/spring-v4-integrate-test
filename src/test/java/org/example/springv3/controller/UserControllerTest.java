package org.example.springv3.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springv3.user.UserRequest;
import org.hamcrest.Matchers;
import org.hamcrest.text.MatchesPattern;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//spring 트랜잭션을 넣어준다. DataJpa? 테스트 할 때는 내부에 Transactional이 내부에 붙어 있었지만 SpringBootTest에는 없으므로 붙여 준다.
//테스트 코드에서는 트랜잭션을 붙이면 롤백 시켜준다. 그러면 회원가입과 로그인이 격리된다. 이때 로그인은 더미 계정으로 로그인 테스트 하는 것이다. 격리!
@Transactional
//아래 2 어노테이션은 항상 고정이다.
//Mock을 빈 컨테이너에 띄우는 것.
@AutoConfigureMockMvc
//MOCK 환경은 가짜 8080포트를 띄우는 것이다.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper om = new ObjectMapper();


    @Test
    public void join_test() throws Exception {
        //given
        UserRequest.JoinDTO joinDTO = new UserRequest.JoinDTO();
        joinDTO.setUsername("haha");
        joinDTO.setPassword("1234");
        joinDTO.setEmail("haha@nate.com");

        //JoinDTO를 json으로 바꾸는 것. 즉 asString으로 읽는 것
        String requestBody = om.writeValueAsString(joinDTO);
        //System.out.println(requestBody);

        //when
        //mockMvc.perform을 해서 특정 주소에 통신을 수행해보는 것이다.
        //주소는 get, post 등을 정하고 post라면 .content가 필요 (우리가 만들어둔 json을 넣어줌)
        //.contentType으로 타입을 정해서 보내준다.
        //그 결과를 actions로 받는 것이다.
        //이때 join 엔드포인트로 POST 요청을 하는 것인데 이는 MockMvcRequestBuilders.post() 메서드를 사용하여 구현하므로
        //MockMvcRequestBuilders를 import 해주면  그냥 mockMvc(post(~))  이런식으로 사용할 수 있다.  get, post, put 등 사용가능
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.post("/join")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        //수행 결과인 응답을 눈으로 확인
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        //System.out.println(responseBody);


        //then
        //아까 우리가 수행한 actions를 andExpect를 통해서 예상 결과를 확인 하는 것.
        //jsonPath를 통해서 json값을 확인하는데 응답코드인 status값을  "$.status" 로 확인하는 것이고 테스트가 성공하므로 응답코드 예상값을 200으로 하면 성공
        //실제고 성공이고 응답코드인 status는 200으로 들어온다. 근데 예상값을 300으로 해두면 실패가 뜨는데 예상값(Expected)은 300, 실제값( aCtual)은 200이 들어오기 때문.
        //참고로 json의 트리를 타고 내려 가려면 점(.)을 찍고 들어가면 된다. 처음은 response의 최상단이고 "$.body.id" 이런식으로 점(.)을 찍고 내려갈 수 있다.
        //이때 MockMvcResultMatchers 도 static import 해줄 수 있다.
        //then에서 상태 검증을 하는 이유가 우리가 항상 결과값을 눈으로 확인할 수 없기 때문이다. 그래서 상태 검증을 테스트코드로 짜두는 것이다.
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.id").value(4));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.username").value("haha"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.email").value("haha@nate.com"));
        //결과값이 null일 때는 value(null) 하는 것이 아니고 value 대신 .isEmpty()로 검증한다.
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.profile").isEmpty());
    }



    @Test
    public void login_test() throws Exception {
        //given
        UserRequest.LoginDTO loginDTO = new UserRequest.LoginDTO();
        loginDTO.setUsername("ssar");
        loginDTO.setPassword("1234");


        //JoinDTO를 json으로 바꾸는 것. 즉 asString으로 읽는 것
        String requestBody = om.writeValueAsString(loginDTO);
        System.out.println(requestBody);

        //when
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        //수행 결과인 응답을 눈으로 확인
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        String responseJwt = actions.andReturn().getResponse().getHeader("Authorization");
        System.out.println(responseJwt);




        //then
        //헤더는 확인하는 방법이 다르다. 헤더부분을 확인한다. 시작부분이 Bearer로 시작하는지 혹은 이 헤더가 null이 아니면 토큰이 있는 것이다.
        actions.andExpect(header().string("Authorization", Matchers.notNullValue()));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body").isEmpty());


    }


}
