package com.yoyodev.starter.Controllers;

import com.yoyodev.starter.Common.Constants.EndpointConstants;
import com.yoyodev.starter.Model.Response.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstants.API_V1 + "/demo")
public class DemoController extends BaseController {
    @GetMapping
    public ResponseEntity<ResponseWrapper<String>> demo() {
        return getSuccess();
    }
}
