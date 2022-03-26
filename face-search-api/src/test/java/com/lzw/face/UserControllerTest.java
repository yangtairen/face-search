package com.lzw.face;

import com.alibaba.fastjson.JSON;
import com.lzw.face.common.ApiResponse;
import com.lzw.face.controller.UserController;
import com.lzw.face.dto.UserRegisterParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Description：
 *
 * @author: YTR
 * @create: 2022-03-26 15:51
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {com.lzw.face.FaceSearchApiApplication.class}, properties = {"application.yml"})
public class UserControllerTest {

    private static final String EMAIL = "844289831@qq.com";
    @Autowired
    private UserController userController;

    @Test
    public void sendCodeTest(){
        ApiResponse apiResponse = userController.sendCode(EMAIL);
        System.out.println(JSON.toJSONString(apiResponse));
    }


    @Test
    public void userRegisterTest(){
        UserRegisterParam userRegisterParam = new UserRegisterParam();
        userRegisterParam.setEmail(EMAIL);
        userRegisterParam.setCode("9BR789");
        userRegisterParam.setTel("1234564878");
        userRegisterParam.setName("try");
        userRegisterParam.setCompany("T-company");

        ApiResponse apiResponse = userController.userRegister(userRegisterParam);
        System.out.println(JSON.toJSONString(apiResponse));
    }

}
