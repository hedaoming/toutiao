package zhku.peishen.toutiao.dao;

import org.apache.ibatis.annotations.*;
import zhku.peishen.toutiao.model.LoginTicket;

/**
 * Created by ipc on 2017/7/16.
 */
@Mapper
public interface LoginTicketDao {
    String TABLE_NAME = "login_ticket";
    String INSERT_FIELDS = " user_id,ticket,expired,status ";
    String SELECT_FIELDS = " id,"+INSERT_FIELDS;

    //insert into login_ticket() values()
    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELDS,") values(#{userId},#{ticket},#{expired},#{status})"})
    int addTicket(LoginTicket loginTicket);

    //update login_ticket set status=? where ticket=?
    @Update({"update ",TABLE_NAME,"set status=#{status} where ticket=#{ticket}"})
    void updateStatus(@Param("ticket") String ticket,@Param("status") int status);

    //select * from login_ticket where ticket = ?
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where ticket=#{ticket}"})
    LoginTicket selectByTicket(@Param("ticket") String ticket);
}
