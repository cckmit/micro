package micro.service.demo.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import micro.commons.concurrent.ConcurrentLock;
import micro.commons.log.MicroLogger;
import micro.commons.page.PageParameter;
import micro.commons.page.Pages;
import micro.commons.util.PageHelperUtils;
import micro.dao.intf.DemoDao;
import micro.bean.po.User;
import micro.service.demo.DemoService;

/**
 * 示例Demo
 * 
 * @author gewx
 **/
@Service
public class DemoServiceImpl implements DemoService {

	private static final MicroLogger LOGGER = new MicroLogger(DemoServiceImpl.class);

	@Autowired
	private DemoDao demoDao;

	@Autowired
	private ConcurrentLock concurrentLock;

	/**
	 * 支持并发处理
	 **/
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public void syncDataById(int id) {
		final String methodName = "syncDataForId";
		LOGGER.enter(methodName, "业务数据同步服务[start], syncId: " + id);

		concurrentLock.key("concurrent").timeOut(5).execute(() -> {
			User u = new User();
			u.setId(String.valueOf(System.currentTimeMillis()));
			u.setUserName("Java");
			int result = demoDao.save(u);
			LOGGER.info("新用户同步成功,id: " + u.getId() + ", result: " + result);

			User update = new User();
			update.setId(u.getId());
			update.setUserName("JavaNew");

			int updateLine = demoDao.updateById(update);
			LOGGER.info("更新行数,line: " + updateLine);
			return null;
		});

		LOGGER.exit(methodName, "业务数据同步服务[end]");
	}

	@Override
	public User getUserById(String userName) {
		return demoDao.getUserById(userName);
	}

	@Override
	public Pages<User> listUser() {
		final String methodName = "listUser";
		LOGGER.enter(methodName, "业务执行");
		PageParameter parameter = new PageParameter();
		parameter.setStartpage(1);
		parameter.setPagesize(20);

		Pages<User> pages = PageHelperUtils.limit(parameter, () -> {
			return demoDao.listUser(1024L);
		});

		LOGGER.exit(methodName, StringUtils.EMPTY);
		return pages;
	}
}
