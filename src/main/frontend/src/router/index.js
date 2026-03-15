import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory("/employment/"),
  routes: [
    {
      path: "/",
      name: "Home",
      component: () => import("../views/Login.vue"),
    },
    {
      path: "/login",
      name: "Login",
      component: () => import("../views/Login.vue"),
    },
    {
      path: "/index.html",
      name: "LoginHtml",
      component: () => import("../views/Login.vue"),
    },
    {
      path: "/index.html*",
      name: "LoginHtmlWithParams",
      component: () => import("../views/Login.vue"),
    },
    {
      path: "/dashboard",
      name: "Dashboard",
      component: () => import("../views/Dashboard.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/admin/users",
      name: "UserManagement",
      component: () => import("../views/admin/UserManagement.vue"),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: "/admin/companies",
      name: "CompanyManagement",
      component: () => import("../views/admin/CompanyManagement.vue"),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: "/admin/statistics",
      name: "Statistics",
      component: () => import("../views/admin/Statistics.vue"),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: "/student/jobs",
      name: "StudentJobs",
      component: () => import("../views/student/StudentJobs.vue"),
      meta: { requiresAuth: true, requiresStudent: true },
    },
    {
      path: "/student/applications",
      name: "StudentApplications",
      component: () => import("../views/student/StudentApplications.vue"),
      meta: { requiresAuth: true, requiresStudent: true },
    },
    {
      path: "/company/jobs",
      name: "CompanyJobs",
      component: () => import("../views/company/CompanyJobs.vue"),
      meta: { requiresAuth: true, requiresCompany: true },
    },
    {
      path: "/company/applications",
      name: "CompanyApplications",
      component: () => import("../views/company/CompanyApplications.vue"),
      meta: { requiresAuth: true, requiresCompany: true },
    },
    {
      path: "/company/profile",
      name: "CompanyProfile",
      component: () => import("../views/company/CompanyProfile.vue"),
      meta: { requiresAuth: true, requiresCompany: true },
    },
    {
      path: "/:pathMatch(?!api)(.*)*",
      redirect: "/dashboard",
    },
  ],
});

// 路由守卫
router.beforeEach((to, from, next) => {
  // 跳过API路径的路由处理
  if (to.path.startsWith("/api")) {
    console.log("API路径，跳过路由处理");
    next();
    return;
  }

  const isAuthenticated = localStorage.getItem("username");
  console.log("路由守卫检查:", {
    to: to.path,
    isAuthenticated: !!isAuthenticated,
    isAdmin: localStorage.getItem("isAdmin"),
    isStudent: localStorage.getItem("isStudent"),
    isCompany: localStorage.getItem("isCompany"),
  });

  // 检查是否是登录页面
  if (
    to.path === "/" ||
    to.path === "/index.html" ||
    to.path === "/login" ||
    to.path.startsWith("/index.html?")
  ) {
    // 如果已经登录，重定向到 dashboard
    if (isAuthenticated) {
      console.log("已认证，重定向到 dashboard");
      next("/dashboard");
    } else {
      console.log("未认证，继续访问登录页");
      next();
    }
    return;
  }

  // 检查是否需要认证
  if (to.meta.requiresAuth && !isAuthenticated) {
    console.log("未认证，重定向到登录页");
    next("/index.html");
    return;
  }

  // 检查管理员权限
  if (to.meta.requiresAdmin && localStorage.getItem("isAdmin") !== "true") {
    console.log("不是管理员，重定向到仪表盘");
    next("/dashboard");
    return;
  }

  // 检查学生权限
  if (to.meta.requiresStudent && localStorage.getItem("isStudent") !== "true") {
    console.log("不是学生，重定向到仪表盘");
    next("/dashboard");
    return;
  }

  // 检查企业权限
  if (to.meta.requiresCompany && localStorage.getItem("isCompany") !== "true") {
    console.log("不是企业，重定向到仪表盘");
    next("/dashboard");
    return;
  }

  // 其他情况，继续访问
  console.log("认证通过，继续访问");
  next();
});

export default router;
