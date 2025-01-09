import { defineStore } from "pinia";
import { ref, computed } from 'vue'

const useUserStore = defineStore("user", () => {
    const email = ref("");
    function setEmail(value) {
        email.value = value;
    }
    const isLogin = computed(function() {
        if(email.value!=null && email.value!="") {
            return true;
        } else {
            return false;
        }
    });
    return {
        email, setEmail, isLogin,
    }
}, {
    persist: {
        storage: sessionStorage,
        key: "vue-user-key"
    }
});

export default useUserStore;
