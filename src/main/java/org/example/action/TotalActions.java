package org.example.action;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.entity.Account;
import org.example.entity.RespErrorCode;
import org.example.entity.RespResult;
import org.example.mapper.AccountMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api")
public class TotalActions {
    @Resource
    AccountMapper accountMapper;

    @PostMapping("/userLogin")
    public RespResult<Account> userLogin(String phone, String code) {
        QueryWrapper<Account> query = new QueryWrapper<Account>();
        Account account = accountMapper.selectOne(query);
        RespResult<Account> result = new RespResult<Account>();
        if (account == null) {
            result.setCode(RespErrorCode.UNREGISTER.getCode());
            result.setMessage(RespErrorCode.UNREGISTER.getMessage());
        } else  {
            result.setCode(RespErrorCode.SUCCESS.getCode());
            result.setMessage(RespErrorCode.SUCCESS.getMessage());
        }
        result.setData(account);
        return result;
    }

}
