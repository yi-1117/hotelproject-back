import { createRouter, createWebHistory } from 'vue-router'

import Home from '@/views/Home.vue';
import NotFound from '@/views/NotFound.vue';
import Forbidden from '@/views/Forbidden.vue';
import Login from '@/views/secure/Login.vue';
import Products from '@/views/pages/Products.vue';

const routes = [
    { path: "/", component: Home, name: "home-link" },
    { path: "/:pathMatch(.*)", component: NotFound, name: "404-link" },
    { path: "/403", component: Forbidden, name: "403-link" },

    { path: "/secure/login", component: Login, name: "secure-login-link" },
    { path: "/pages/product", component: Products, name: "pages-products-link" }
];

export default createRouter({
    routes: routes,
    history: createWebHistory(),
});
