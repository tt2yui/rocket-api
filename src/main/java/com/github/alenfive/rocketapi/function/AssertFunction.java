package com.github.alenfive.rocketapi.function;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 异常断言函数
 */
@Component
public class AssertFunction implements IFunction{

    @Override
    public String getVarName() {
        return "Assert";
    }

    public void isNotEmpty(Object input,String ... msg){
        boolean validate = !StringUtils.isEmpty(input);
        if (!validate){
            String throwMsg = msg.length>0?msg[0]:("`"+input+"` is empty");
            throw new RuntimeException(throwMsg);
        }
    }

    public void isEmpty(Object input,String ... msg){
        boolean validate = StringUtils.isEmpty(input);
        if (!validate){
            String throwMsg = msg.length>0?msg[0]:("`"+input+"` is not empty");
            throw new RuntimeException(throwMsg);
        }
    }

    public void equals(Object input,Object input2,String ... msg){
        boolean validate = Objects.equals(input,input2);
        if (!validate){
            String throwMsg = msg.length>0?msg[0]:("`"+input+"` is not equals");
            throw new RuntimeException(throwMsg);
        }
    }

    public void isTrue(boolean validate,String ... msg){
        if (!validate){
            String throwMsg = msg.length>0?msg[0]:("input is false");
            throw new RuntimeException(throwMsg);
        }
    }

    public void regex(String regex,String input,String ... msg){
        boolean validate = input == null?false:input.matches(regex);
        if (!validate){
            String throwMsg = msg.length>0?msg[0]:("`"+input+"`is not matches");
            throw new RuntimeException(throwMsg);
        }
    }


}
