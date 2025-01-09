<template>
	<h3>Login (Vue)</h3>
	<table>
        <tbody>
            <tr>
                <td>ID : </td>
                <td><input type="text" v-model="username"></td>
                <td><span class="error">{{ message }}</span></td>
            </tr>
            <tr>
                <td>PWD : </td>
                <td><input type="text" v-model="password"></td>
                <td></td>
            </tr>
            <tr>
                <td> </td>
                <td align="right"><button type="button" @click="login">login</button></td>
            </tr>
        </tbody>
	</table>
</template>
    
<script setup>
    import axiosapi from '@/plugins/axios.js'
    import Swal from 'sweetalert2';
    import { ref } from 'vue';
    import { useRouter } from 'vue-router';
    import useUserStore from '@/stores/user.js'

    const router = useRouter();
    const message = ref("");
    const username = ref("");
    const password = ref("");
    const userStore = useUserStore();

    async function login() {
        message.value = "";

        if (username.value === "") {
            username.value = null;
        }
        if (password.value === "") {
            password.value = null;
        }
        const body = {
            "username": username.value,
            "password": password.value
        };
        
        axiosapi.defaults.headers.authorization = "";
        userStore.setEmail("");
        try {
            const response = await axiosapi.post("/ajax/secure/login", body);
            console.log("response", response);
            if (response.data.success) {
                await Swal.fire({
                    title: response.data.message,
                    icon: "success"
                });
                axiosapi.defaults.headers.authorization = "Bearer "+ response.data.token;
                userStore.setEmail(response.data.user);
                router.push({ path: '/'});
            } else {
                message.value = response.data.message;
                Swal.fire({
                    title: response.data.message,
                    icon: "warning"
                });
            }
        } catch (error) {
            console.log("error", error);
            Swal.fire({
                title: "執行失敗:" + error.message,
                icon: "error"
            });
        }
    }
</script>
    
<style>
    
</style>