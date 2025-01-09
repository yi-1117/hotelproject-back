<template>
    <h3>首頁 {{ userStore.email }}</h3>
</template>
    
<script setup>
    import useUserStore from '@/stores/user.js'
    const userStore = useUserStore();

</script>
    
<style>
    
</style>