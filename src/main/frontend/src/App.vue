<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from "vue";

onMounted(async () => {
  try {
    // 从本地存储获取用户ID
    const userId = localStorage.getItem("userId");

    if (userId) {
      // 检查用户是否已登录
      const response = await fetch(`/employment/api/users/${userId}`);

      if (response.ok) {
        const user = await response.json();
        if (user) {
          // 存储用户信息
          localStorage.setItem("username", user.username);
          localStorage.setItem(
            "isAdmin",
            (
              user.role === "ADMIN" ||
              user.role === "admin" ||
              user.role === "Admin"
            ).toString(),
          );
          localStorage.setItem(
            "isStudent",
            (
              user.role === "STUDENT" ||
              user.role === "student" ||
              user.role === "Student"
            ).toString(),
          );
          localStorage.setItem(
            "isCompany",
            (
              user.role === "COMPANY" ||
              user.role === "company" ||
              user.role === "Company"
            ).toString(),
          );

          // 如果是公司用户，存储公司ID
          if (user.role === "COMPANY" && user.companyId) {
            localStorage.setItem("companyId", user.companyId.toString());
          }

          console.log("用户已登录，获取用户信息:", user);
        }
      }
    }
  } catch (error) {
    console.error("检查用户登录状态失败:", error);
  }
});
</script>

<style>
#app {
  min-height: 100vh;
  background-color: var(--light-color);
}
</style>
