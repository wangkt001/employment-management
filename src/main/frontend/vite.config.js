import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import { resolve } from "path";

// https://vite.dev/config/
export default defineConfig({
  base: "/employment",
  plugins: [vue()],
  resolve: {
    alias: {
      "@": resolve(__dirname, "src"),
    },
  },
  build: {
    outDir: "../resources/static",
    emptyOutDir: true,
  },
  server: {
    port: 3000,
    open: true,
    historyApiFallback: {
      rewrites: [
        {
          from: /^\/employment\/.*/,
          to: "/employment/index.html",
        },
      ],
    },
    proxy: {
      "/api": {
        target: "http://localhost:8080/employment",
        changeOrigin: true,
      },
      "/employment/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/api/applications": {
        target: "http://localhost:8080/employment",
        changeOrigin: true,
      },
      "/employment/applications": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/employment/login": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/employment/logout": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
