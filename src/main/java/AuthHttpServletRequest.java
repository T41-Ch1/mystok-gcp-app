package pac1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

//ソルトを使用してもgetRemoteUserやisUserInRoleを使うためにHttpServletRequestWrapperを継承して作る
public class AuthHttpServletRequest extends HttpServletRequestWrapper {

    public AuthHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
	public String getRemoteUser() {
        HttpSession session = this.getSession(false);
        if (session == null) {
            return null;
        }
        return (String)session.getAttribute("auth.user");
    }

    @Override
	public boolean isUserInRole(String str) {
        if (str == null) {
            return false;
        }
        HttpSession session = this.getSession(false);
        if (session == null) {
            return false;
        }

        Object role = session.getAttribute("auth.role");
        return (str.equals(role));
    }

}