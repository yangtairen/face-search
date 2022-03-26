package com.lzw.face;

import com.alibaba.fastjson.JSON;
import com.lzw.face.common.ApiResponse;
import com.lzw.face.controller.UserPersonController;
import com.lzw.face.dto.FaceSearchParam;
import com.lzw.face.dto.PersonRegisterParam;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Description：
 *
 * @author: YTR
 * @create: 2022-03-26 15:23
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FaceSearchApiApplication.class}, properties = {"application.yml"})
public class UserPersonControllerTest {

    private static String faceImagePath = "C:\\Users\\YTR\\Desktop\\test\\pengyuyan.jpeg";
//    private static String faceImagePath = "C:\\Users\\YTR\\Desktop\\test\\liudehua.jpeg";
//    private static String faceImagePath = "C:\\Users\\YTR\\Desktop\\test\\liuyifei.jpeg";
    private static String openkey = "113c5a52599d49928b5bed0e8e5772e4";

    private String faceImage;
    @Autowired
    private UserPersonController userPersonController;

    @Before
    public void before() throws IOException {

        byte[] bytes = Files.readAllBytes(new File(faceImagePath).toPath());
        faceImage = Base64.getEncoder().encodeToString(bytes);
    }

    @Test
    public void testFaceRegister(){
        List<String> images = Arrays.asList(faceImage);

        String tag = com.google.common.io.Files.getNameWithoutExtension(faceImagePath);;
        PersonRegisterParam personRegisterParam = PersonRegisterParam.builder()
                .imgData(images)
                .openKey(openkey)
                .personTag(tag).build();
        ApiResponse<Object> objectApiResponse = userPersonController.faceRegister(personRegisterParam);
        System.out.println(JSON.toJSONString(objectApiResponse));

    }

    @Test
    public void testFaceSearch(){
        FaceSearchParam param = FaceSearchParam.builder().img(faceImage).openKey(openkey).build();
        ApiResponse<Object> objectApiResponse = userPersonController.faceSearch(param);
        System.out.println(JSON.toJSONString(objectApiResponse));
    }

}
