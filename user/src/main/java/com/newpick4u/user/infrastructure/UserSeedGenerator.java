//package com.newpick4u.user.infrastructure;
//
//import com.newpick4u.user.application.usecase.UserService;
//import com.newpick4u.user.application.dto.request.CreateUserRequestDto;
//import com.newpick4u.user.application.dto.request.SignInUserRequestDto;
//import com.newpick4u.user.application.dto.response.SignInUserResponseDto;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.io.FileWriter;
//import java.util.ArrayList;
//import java.util.List;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class UserSeedGenerator {
//
//    private final UserService userService;
//
//    @PostConstruct // 서버 뜰 때 자동으로 한번 실행
//    public void seedUsers() {
//        try {
//            List<String> tokens = new ArrayList<>();
//            for (int i = 1; i <= 100; i++) {
//                String username = "testuser" + i;
//                String password = "testpass" + i;
//                String name = "Test User " + i;
//
//                // 1. 유저 생성
//                CreateUserRequestDto createDto = new CreateUserRequestDto(username, password, name);
//                userService.createUser(createDto);
//
//                // 2. 로그인 → 토큰 생성
//                SignInUserRequestDto signInDto = new SignInUserRequestDto(username, password);
//                SignInUserResponseDto signInResponse = userService.signInUser(signInDto);
//
//                tokens.add(signInResponse.accessToken());
//            }
//
//            // 3. 토큰 저장
//            saveTokensToFile(tokens);
//            log.info("[UserSeed] 테스트 유저 100명 생성 및 토큰 저장 완료");
//        } catch (Exception e) {
//            log.error("[UserSeed] 테스트 유저 생성 실패", e);
//        }
//    }
//
//    private void saveTokensToFile(List<String> tokens) throws Exception {
//        try (FileWriter writer = new FileWriter("k6-user-tokens.txt")) {
//            for (String token : tokens) {
//                writer.write(token + "\n");
//            }
//        }
//    }
//}
