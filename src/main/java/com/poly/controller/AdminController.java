package com.poly.controller;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.poly.bean.*;
import com.poly.service.*;
import com.poly.util.*;

@Controller
public class AdminController {

	@Autowired
	ParamService paramService;

	@Autowired
	UsersService userService;

	@Autowired
	AuthService authService;

	@Autowired
	RoleService roleService;

	@Autowired
	RanksService rankService;

	@Autowired
	PaymentService payService;

	@Autowired
	ServicePackService ServicePackService;

	@Autowired
	PostService postService;

	@Autowired
	AlbumsService albumsServcie;

	@Autowired
	TypePropertyService typePropertyService;

	@Autowired
	DetailTransactionService detailTransactionService;

	@Autowired
	TransactionService transactionService;

	@Autowired
	SessionService ss;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private PaymentService Pay;

	@Autowired
	MailerService mailService;

	// Home
	@RequestMapping({ "/admin", "/admin/index" })
	public String getHome(Model m) {
		LocalDate currentDate = LocalDate.now();
		int currentYear = currentDate.getYear();

		List<DetailTransactions> listPay = detailTransactionService.findAllDetailTransactionPay();
		List<DetailTransactions> listPost = detailTransactionService.findAllDetailTransactionPost();
		List<Object[]> listCPMonth = transactionService.getCurrentAndPreviousMonth();

		m.addAttribute("totalYear", transactionService.getTotalYear(currentYear));
		m.addAttribute("listCPMonth", listCPMonth);
		m.addAttribute("details", listPay);
		m.addAttribute("detailPost", listPost);
		m.addAttribute("u", ss.getAttribute("user"));
		return "admin/index";
	}
	// Home

	// User List
	@RequestMapping("/admin/users")
	public String getUsers(Model m, @RequestParam(defaultValue = "1") int page) {
		Users u = ss.getAttribute("user");
		Pageable pageable = PageRequest.of(page - 1, 4);
		Page<Users> users = userService.findAll(u.getUsername(), pageable);

		if (ss.getAttribute("err") != null) {
			m.addAttribute("visible", "true");
			m.addAttribute("title", ss.getAttribute("title"));
			m.addAttribute("notification", ss.getAttribute("notification"));
			m.addAttribute("users", users);
			m.addAttribute("u", new Users());
			return "admin/users";
		} else {
			m.addAttribute("visible", "false");
			m.addAttribute("users", users);
			m.addAttribute("u", new Users());
			return "admin/users";
		}
	}

	@PostMapping("/admin/add-user")
	public String Register(Model m, Users u, @Param("username") String username, @Param("email") String email,
			@Param("phone") String phone, @Param("role") String role) throws MessagingException {

		Users uFindEmail = userService.findByEmailOrPhone(email, null);
		Users uFindPhone = userService.findByEmailOrPhone(null, phone);
		Users findId = userService.findById(username);
		
		if (findId != null) {
			ss.setAttribute("err", "Error");

			ss.setAttribute("title", "Lỗi");
			ss.setAttribute("notification", "Tên đăng nhập đã được đăng ký");
			return "redirect:/admin/users";
		} else if (uFindPhone != null) {
			ss.setAttribute("err", "Error");

			ss.setAttribute("title", "Lỗi");
			ss.setAttribute("notification", "Số điện thoại đã được đăng ký");
			return "redirect:/admin/users";
		} else if (uFindEmail != null) {
			ss.setAttribute("err", "Error");

			ss.setAttribute("title", "Lỗi");
			ss.setAttribute("notification", "Email đã được đăng ký");
			return "redirect:/admin/users";
		} else {
			Pay newpay = new Pay();
			newpay.setPay_money((long) 0.00);
			payService.Create(newpay);
			
			Pay payFind = payService.findByTop1Desc();

			// rank
			Ranks rank = rankService.findById(1);

			String password = paramService.getString("passwords", "");

			u.setPasswords(passwordEncoder.encode(password));
			u.setPay_id(payFind);
			u.setRanks_id(rank);
			u.setActive(false);
			userService.create(u);

			Auth uAuth = new Auth();
			uAuth.setUsers(u);
			uAuth.setRoles(roleService.findbyId(role));
			authService.create(uAuth);
			
			ss.setAttribute("usermail", u.getEmail());

			// gui mail kich hoat tai khoan
			mailService.sendMailConfirm(u.getEmail(), "Kích hoạt tài khoản", u.getFullname(), null);
			// gui mail kich hoat tai khoan

			ss.setAttribute("err", "Error");

			ss.setAttribute("title", "Thành công");
			ss.setAttribute("notification", "Thêm tài khoản thành công");

			return "redirect:/admin/users";
		}

	}

	@RequestMapping("/admin/users-new")
	public String setUserNew(Model m) {
		return "redirect:/admin/users";
	}

	@RequestMapping("/admin/user/findBy/{username}")
	public String getUsers(Model m, @PathVariable String username, @RequestParam(defaultValue = "1") int page) {
		Users u = ss.getAttribute("user");
		Pageable pageable = PageRequest.of(page - 1, 4);
		Page<Users> users = userService.findAll(u.getUsername(), pageable);

		m.addAttribute("users", users);
		m.addAttribute("u", userService.findById(username));
		return "admin/users";
	}

	@PostMapping("/admin/user/update")
	public String getUsersUpdate(Model m, Users u) {
		m.addAttribute("users", userService.findAll());
		Users us = userService.findById(u.getUsername());
		Pay p = payService.findByID(us.getPay_id().getPay_id());
		Ranks r = rankService.findById(us.getRanks_id().getRanks_id());
		u.setPay_id(p);
		u.setRanks_id(r);
		u.setActive(true);
		userService.update(u);
		m.addAttribute("u", userService.findById(u.getUsername()));
		return "redirect:/admin/user/findBy/" + u.getUsername();
	}

	@RequestMapping("/admin/user/delete/{username}")
	public String setAccountUser(Model m, @PathVariable String username) {
		Users u = userService.findById(username);
		u.setActive(false);
		u.setCreate_block(new Date());
		userService.update(u);
		return "redirect:/admin/user/findBy/" + u.getUsername();
	}
	// User List

	// Post List
	@RequestMapping({ "/admin/post", "/admin/post-list" })
	public String getPostList(Model m, @RequestParam(defaultValue = "1") Integer page) {
		Pageable pageable = PageRequest.of(page - 1, 4);
		Page<Post> managerPost = postService.getPageAll(pageable);
		if(ss.getAttribute("err") == null) {
			m.addAttribute("post", new Post());
			m.addAttribute("managerPost", managerPost);
			return "admin/post";
		}else {
			m.addAttribute("visible", "true");
			m.addAttribute("title", ss.getAttribute("title"));
			m.addAttribute("notification", ss.getAttribute("notification"));
			m.addAttribute("post", new Post());
			m.addAttribute("managerPost", managerPost);
			return "admin/post";
		}
		
	}

	@RequestMapping("/admin/post-find")
	public String getPostList(Model m, @Param("id") Integer id, @RequestParam(defaultValue = "1") Integer page) {
		Pageable pageable = PageRequest.of(page - 1, 4);
		Page<Post> managerPost = postService.getPageAll(pageable);

		m.addAttribute("post", postService.getFindByid(id));
		m.addAttribute("albums", albumsServcie.findAlbumsByPostID(id));
		m.addAttribute("managerPost", managerPost);
		return "admin/post";
	}

	@RequestMapping("/admin/post-find-delete")
	public String getPostDelete(Model m, @Param("id") Integer id) {

		postService.SoftDeletePost(id);
		ss.setAttribute("err", "Error");
		ss.setAttribute("title", "Thành công");
		ss.setAttribute("notification", "Xóa thành công");
		return "redirect:/admin/post";
	}

	@RequestMapping("/admin/post-find-update")
	public String getPostUpdate(Model m, @Param("id") Integer id) {
		Post p = postService.getFindByid(id);
		p.setDeletedAt(false);
		postService.Update(p);
		ss.setAttribute("err", "Error");
		ss.setAttribute("title", "Thành công");
		ss.setAttribute("notification", "Cập nhật thành công");
		return "redirect:/admin/post";
	}

	@RequestMapping("/admin/history-delete-post")
	public String getHistoryDeletePostList(Model m, @RequestParam(defaultValue = "1") Integer page) {
		Pageable pageable = PageRequest.of(page - 1, 4);
		Page<Post> managerPost = postService.getPostDelete(pageable);
		m.addAttribute("managerPost", managerPost);
		return "admin/historyDeletePost";
	}

	// Post List

	// Transactions List
	@RequestMapping({ "/admin/transactions", "/admin/transaction-list" })
	public String getTransactions(Model m, @RequestParam(defaultValue = "1") int page) {
		Pageable pageable = PageRequest.of(page - 1, 4);
		Page<DetailTransactions> detailTransactions = detailTransactionService.findAll(pageable);
		m.addAttribute("transactions", detailTransactions);
		m.addAttribute("transaction", new DetailTransactions());
		return "admin/transactions";
	}

	@RequestMapping("/admin/transaction/findBy/{id}")
	public String getTransactions(Model m, @PathVariable("id") Integer id, @RequestParam(defaultValue = "1") int page) {
		Pageable pageable = PageRequest.of(page - 1, 4);
		Page<DetailTransactions> detailTransactions = detailTransactionService.findAll(pageable);
		m.addAttribute("transactions", detailTransactions);
		m.addAttribute("transaction", detailTransactionService.findById(id));
		return "admin/transactions";
	}
	// Transactions List

	// Wallet List
	@RequestMapping({ "/admin/wallet", "/admin/wallet-list" })
	public String getWalletList(Model m) {
		List<Users> u = userService.findAll();
		m.addAttribute("pays", u);
		Users user = new Users();
		user.setPay_id(new Pay());
		m.addAttribute("user", user);
		return "admin/wallet";
	}

	@RequestMapping("/admin/wallet-new")
	public String getWalletNew(Model m) {
		return "redirect:/admin/wallet";
	}

	@RequestMapping("/find/users/{id}")
	public String getWalletList(Model m, @PathVariable("id") String username) {
		List<Users> u = userService.findAll();
		m.addAttribute("pays", u);

		Users user = userService.findById(username);
		m.addAttribute("user", user);
		return "admin/wallet";
	}

	@RequestMapping("/admin/user-update")
	public String getWalletUpdate(Model m, @Param("username") String username, @Param("pay") long pay) {
		List<Users> u = userService.findAll();
		m.addAttribute("pays", u);

		Users user = userService.findById(username);
		m.addAttribute("user", user);

		Pay p = Pay.findByID(user.getPay_id().getPay_id());
		p.setPay_money(pay);
		Pay.Update(p);
		
		m.addAttribute("visible", "true");
		m.addAttribute("title", "Thành công");
		m.addAttribute("notification", "Cập nhật thành công");
		
		return "admin/wallet";
	}
	// Wallet List

	// Profile User (Admin)
	@RequestMapping("/admin/profile")
	public String getProfile(Model m) {
		Users u = ss.getAttribute("user");
		if(ss.getAttribute("err") == null) {
			m.addAttribute("u", userService.findById(u.getUsername()));
			return "admin/profile";
		}else {
			m.addAttribute("visible", "true");
			m.addAttribute("title", ss.getAttribute("title"));
			m.addAttribute("notification", ss.getAttribute("notification"));
			m.addAttribute("u", userService.findById(u.getUsername()));
			return "admin/profile";
		}
		
	}

	@RequestMapping("/admin/profile-edit")
	public String setProfile(Model m, Users u, 
		@Param("email") String email, @Param("phone") String phone) {
		
		Users uFindEmail = userService.findByEmailOrPhone(email, null);
		Users uFindPhone = userService.findByEmailOrPhone(null, phone);
		
		if (uFindPhone != null) {
			ss.setAttribute("err", "Error");
			ss.setAttribute("title", "Lỗi");
			ss.setAttribute("notification", "Số điện thoại đã được đăng ký");
			return "redirect:/admin/profile";
		} else if (uFindEmail != null) {
			ss.setAttribute("err", "Error");
			ss.setAttribute("title", "Lỗi");
			ss.setAttribute("notification", "Email đã được đăng ký");
			return "redirect:/admin/profile";
		} else {
			Users uFind = userService.findById(u.getUsername());
			Pay p = payService.findByID(uFind.getPay_id().getPay_id());
			Ranks r = rankService.findById(uFind.getRanks_id().getRanks_id());
			u.setPay_id(p);
			u.setRanks_id(r);
			u.setActive(true);
			userService.update(u);
			ss.setAttribute("user", u);
			ss.setAttribute("err", "Error");
			ss.setAttribute("title", "Thành công");
			ss.setAttribute("notification", "Cập nhật thành công");
			return "redirect:/admin/profile";
		}
		
	}

	@RequestMapping("/admin/ChangePass")
	public String setChangePass(Model m, Users u) {
		Users user = (Users) ss.getAttribute("user");

		String passmoi = paramService.getString("passmoi", "");
		String nhaplaipassmoi = paramService.getString("nhaplaipassmoi", "");

		if (passmoi.equalsIgnoreCase(nhaplaipassmoi)) {
			u.setPasswords(passwordEncoder.encode(passmoi));// db
			user.setPasswords(passwordEncoder.encode(passmoi));// session
			userService.update(user);
			ss.setAttribute("err", "Error");
			ss.setAttribute("title", "Thành công");
			ss.setAttribute("notification", "Cập nhật thành công");
			return "redirect:/admin/profile";
		} else {
			ss.setAttribute("err", "Error");
			ss.setAttribute("title", "Lỗi");
			ss.setAttribute("notification", "Mật khẩu nhập lại không khớp");
			return "redirect:/admin/profile";
		}
	}
	// Profile User (Admin)

	// Service Pack
	@RequestMapping("/admin/service-pack")
	public String getServicePack(Model m, @RequestParam(defaultValue = "1") Integer page) {
		Pageable pageable = PageRequest.of(page - 1, 4);
		Page<ServicePack> service = ServicePackService.getFindAll(pageable);
		if(ss.getAttribute("err") == null) {
			m.addAttribute("service", service);
			m.addAttribute("totalNumber", service.getTotalElements());
			m.addAttribute("visible", "false");
			return "admin/servicePack";
		}else {
			
			m.addAttribute("service", service);
			m.addAttribute("totalNumber", service.getTotalElements());
			m.addAttribute("visible", "true");
			m.addAttribute("title", ss.getAttribute("title"));
			m.addAttribute("notification", ss.getAttribute("notification"));
			return "admin/servicePack";
		}
		
	}

	@PostMapping("/admin/add-service-pack")
	public String addServicePack(Model m, ServicePack s) {
		ServicePackService.Create(s);
		ss.setAttribute("err", "Error");
		ss.setAttribute("title", "Thành công");
		ss.setAttribute("notification", "Thêm gói dịch vụ thành công");
		return "redirect:/admin/service-pack";
	}

	@PostMapping("/admin/update-service-pack")
	public String updateServicePack(Model m, ServicePack s) {
		ServicePackService.Update(s);
		ss.setAttribute("err", "Error");
		ss.setAttribute("title", "Thành công");
		ss.setAttribute("notification", "Cập nhật gói dịch vụ thành công");
		return "redirect:/admin/service-pack";
	}

	@GetMapping("/admin/delete-service-pack/{id}")
	public String deleteServicePack(Model m, @PathVariable("id") Integer id) {
		ServicePackService.Delete(id);
		return "redirect:/admin/service-pack";
	}

	// Service Pack
	// Type Propertys
	@RequestMapping("/admin/type-property")
	public String getTypeProperty(Model m, @RequestParam(defaultValue = "1") Integer page) {
		Pageable pageable = PageRequest.of(page - 1, 6);
		Page<TypePropertys> typeProperty = typePropertyService.findPageAll(pageable);
		
		if(ss.getAttribute("err") == null) {
			m.addAttribute("typeProperty", typeProperty);
			m.addAttribute("totalNumber", typeProperty.getTotalElements());
			m.addAttribute("visible", "false");
			return "admin/typePropertys";
		}else {
			
			m.addAttribute("typeProperty", typeProperty);
			m.addAttribute("totalNumber", typeProperty.getTotalElements());
			m.addAttribute("visible", "true");
			m.addAttribute("title", ss.getAttribute("title"));
			m.addAttribute("notification", ss.getAttribute("notification"));
			return "admin/typePropertys";
		}
		
	}

	@PostMapping("/admin/add-type-property")
	public String createTypeProperty(Model m, TypePropertys p) {
		typePropertyService.create(p);
		ss.setAttribute("err", "Error");
		ss.setAttribute("title", "Thành công");
		ss.setAttribute("notification", "Thêm loại tài sản thành công");
		return "redirect:/admin/type-property";
	}

	@PostMapping("/admin/update-type-property")
	public String updateTypeProperty(Model m, TypePropertys p) {
		typePropertyService.update(p);
		ss.setAttribute("err", "Error");
		ss.setAttribute("title", "Thành công");
		ss.setAttribute("notification", "Cập nhật loại tài sản thành công");
		return "redirect:/admin/type-property";
	}

	@GetMapping("/admin/delete-type-property/{id}")
	public String deleteTypeProperty(Model m, @PathVariable("id") Integer id) {
		typePropertyService.delete(id);
		return "redirect:/admin/type-property";
	}
	// Type Propertys
}
