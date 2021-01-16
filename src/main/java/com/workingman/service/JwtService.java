package com.workingman.service;



import com.workingman.javaBean.RoleBean;
import com.workingman.javaBean.UserBean;
import com.workingman.javaBean.state.RedisHeader;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * JwtService类
 *
 * @author 赵云
 * @date 2020/09/10
 */

@Service
public class JwtService {
    @Autowired
    private RedisTemplate<String, RoleBean> roleBeanRedisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final static SignatureAlgorithm SIGNATURE_ALGORITHM= SignatureAlgorithm.HS256;
    //token存活时间
    private final static Long ACCESS_TOKEN_EXPIRATION = 3600L*1000;

    //refreshToken存活时间
    private final static Long REFRESH_TOKEN_EXPIRATION = 10*24*3600L*1000;

    //jwt的签发者
    private final static String JWT_ISS = "赵云";

    //jwt的所有人
    private final static String SUBJECT = "赵云";

    private final static String secret="faewghrwaoeifqhyr32855";

    /**
     * 获取token
     * @param user:user信息，map类型
     * @return token
     */
    public String getToken(UserBean user){
        Map<String,Object> claims=new HashMap<>();
        claims.put("user",user);
        Map<String,Object> header=new HashMap<>();
        header.put("alg","HS256");
        header.put("typ","JWT");
        return Jwts.builder().setIssuer(JWT_ISS)
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis()+ACCESS_TOKEN_EXPIRATION))
                .setHeader(header)
                .setSubject(SUBJECT)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .signWith(SIGNATURE_ALGORITHM,secret)
                .compact();
    }

    /**
     * 获取refreshToken
     * @param user:user信息，map类型
     * @return refreshToken
     */
    public String getRefreshToken(UserBean user){
        Map<String,Object> claims=new HashMap<>();
        claims.put("user",user);
        Map<String,Object> header=new HashMap<>();
        header.put("alg","HS256");
        header.put("typ","JWT");
        return Jwts.builder().setIssuer(JWT_ISS)
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis()+REFRESH_TOKEN_EXPIRATION))
                .setHeader(header)
                .setSubject(SUBJECT)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .signWith(SIGNATURE_ALGORITHM,secret)
                .compact();
    }

    /**
     * 获取Claims
     * @param token:token
     * @return claims
     */
    public Claims getClaims(String token) throws ExpiredJwtException,Exception{
        Claims claims=null;
        claims= Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims;
    }

    /*public UserDetails getUserDetails(String token) throws ExpiredJwtException{
        Claims claims=Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        int id=claims.get("id",Integer.class);
        String name=claims.get("name",String.class);
        String phone=claims.get("phone",String.class);

    }*/

    /**
     * 获取User对象
     * @param token：token字符串
     * @return UserBean
     * @throws ExpiredJwtException:token过期
     */
    public UserBean getUser(String token) throws ExpiredJwtException,Exception{
        Claims claims=Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        Map<String,Object> userMap= (Map<String, Object>) claims.get("user");
        int id= (int) userMap.get("id");
        String phone=(String)userMap.get("phone");
        return new UserBean(id,phone);
    }

    /**
     * 获取User对象，并存在request中
     * @param token：token字符串
     * @param request：HttpServletRequest
     * @return UserBean
     * @throws ExpiredJwtException:token已过期
     */
    public UserBean getUser(String token, HttpServletRequest request) throws ExpiredJwtException,Exception{
        Claims claims=Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        Map<String,Object> userMap= (Map<String, Object>) claims.get("user");
        int id= (int) userMap.get("id");
        String phone=(String)userMap.get("phone");
        UserBean user=new UserBean(id,phone);
        request.setAttribute("user",user);
        System.out.println(user);
        return user;
    }

    public UserDetails getUserDetails(String token,HttpServletRequest request) throws Exception{
        Claims claims=Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        Map<String,Object> userMap= (Map<String, Object>) claims.get("user");
        int id= (int) userMap.get("id");
        String phone=(String)userMap.get("phone");
        UserBean user=new UserBean(id,phone);
        UserDetails userDetails=null;
        List<RoleBean> roles=roleBeanRedisTemplate.opsForList().range(RedisHeader.ROLE.getHeader()+phone,0,-1);
        if(roles!=null){
            user.setRoles(roles);
            userDetails=User.withUsername(user.getPhone()).password("123456").roles(user.getRolesName()).build();
        }else {
            userDetails=User.withUsername("匿名用户").password("123456").roles("anonymity").build();
        }
        request.setAttribute("user",user);
        System.out.println(user);
        return userDetails;
    }

    public UserDetails getUserDetails(String token) throws Exception{
        Claims claims=Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        Map<String,Object> userMap= (Map<String, Object>) claims.get("user");
        int id= (int) userMap.get("id");
        String phone=(String)userMap.get("phone");
        UserBean user=new UserBean(id,phone);
        String role=stringRedisTemplate.opsForValue().get(RedisHeader.ROLE.getHeader()+phone);
//        user.setRole(role));
//      request.setAttribute("user",user);
        System.out.println(user);
        return null;
    }
}
