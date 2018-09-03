package com.github.zhangkaitao.shiro.chapter11;

import com.github.zhangkaitao.shiro.chapter11.JdbcTemplateUtils;
import com.github.zhangkaitao.shiro.chapter11.entity.Permission;
import com.github.zhangkaitao.shiro.chapter11.entity.Role;
import com.github.zhangkaitao.shiro.chapter11.entity.User;
import com.github.zhangkaitao.shiro.chapter11.service.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Before;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 14-1-28
 * <p>Version: 1.0
 */
public abstract class BaseTest {

    protected PermissionService permissionService = new PermissionServiceImpl();
    protected RoleService roleService = new RoleServiceImpl();
    protected UserService userService = new UserServiceImpl();

    protected String password = "123";

    protected Permission p1;
    protected Permission p2;
    protected Permission p3;
    protected Role r1;
    protected Role r2;
    protected User u1;

    @Before
    public void setUp() {
        JdbcTemplateUtils.jdbcTemplate().update("delete from sys_users");
        JdbcTemplateUtils.jdbcTemplate().update("delete from sys_roles");
        JdbcTemplateUtils.jdbcTemplate().update("delete from sys_permissions");
        JdbcTemplateUtils.jdbcTemplate().update("delete from sys_users_roles");
        JdbcTemplateUtils.jdbcTemplate().update("delete from sys_roles_permissions");


        //1銆佹柊澧炴潈闄�
        p1 = new Permission("user:create", "鐢ㄦ埛妯″潡鏂板", Boolean.TRUE);
        p2 = new Permission("user:update", "鐢ㄦ埛妯″潡淇敼", Boolean.TRUE);
        p3 = new Permission("menu:create", "鑿滃崟妯″潡鏂板", Boolean.TRUE);
        permissionService.createPermission(p1);
        permissionService.createPermission(p2);
        permissionService.createPermission(p3);
        //2銆佹柊澧炶鑹�
        r1 = new Role("admin", "绠＄悊鍛�", Boolean.TRUE);
        r2 = new Role("user", "鐢ㄦ埛绠＄悊鍛�", Boolean.TRUE);
        roleService.createRole(r1);
        roleService.createRole(r2);
        //3銆佸叧鑱旇鑹�-鏉冮檺
        roleService.correlationPermissions(r1.getId(), p1.getId());
        roleService.correlationPermissions(r1.getId(), p2.getId());
        roleService.correlationPermissions(r1.getId(), p3.getId());

        roleService.correlationPermissions(r2.getId(), p1.getId());
        roleService.correlationPermissions(r2.getId(), p2.getId());

        //4銆佹柊澧炵敤鎴�
        u1 = new User("zhang", password);
        userService.createUser(u1);
        //5銆佸叧鑱旂敤鎴�-瑙掕壊
        userService.correlationRoles(u1.getId(), r1.getId());

        //1銆佽幏鍙朣ecurityManager宸ュ巶锛屾澶勪娇鐢↖ni閰嶇疆鏂囦欢鍒濆鍖朣ecurityManager
        Factory<org.apache.shiro.mgt.SecurityManager> factory =
                new IniSecurityManagerFactory("classpath:shiro.ini");

        //2銆佸緱鍒癝ecurityManager瀹炰緥 骞剁粦瀹氱粰SecurityUtils
        org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

    }

    @After
    public void tearDown() throws Exception {
        ThreadContext.unbindSubject();//閫�鍑烘椂璇疯В闄ょ粦瀹歋ubject鍒扮嚎绋� 鍚﹀垯瀵逛笅娆℃祴璇曢�犳垚褰卞搷
    }

    protected void login(String username, String password) {

        //3銆佸緱鍒癝ubject鍙婂垱寤虹敤鎴峰悕/瀵嗙爜韬唤楠岃瘉Token锛堝嵆鐢ㄦ埛韬唤/鍑瘉锛�
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        subject.login(token);
    }

    public Subject subject() {
        return SecurityUtils.getSubject();
    }

}
