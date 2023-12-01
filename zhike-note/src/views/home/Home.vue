<template>
    <div class="home">
        <div class="logo">
            <img src="src\assets\logo.png" alt="Logo" />
        </div>
        <div class="welcome">
            <h1>Welcome to Cloud Notes</h1>
            <p>Start taking notes, anytime, anywhere.</p>
            <router-link to="/note" class="login-btn">NOTE</router-link>
        </div>
    </div>
</template>
  
<style scoped>
.home {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 80vh;
    width: 100%;
}

.logo {
    margin-bottom: 20px;
}

.welcome {
    text-align: center;
}

.login-btn {
    padding: 10px 20px;
    background-color: #007bff;
    color: white;
    text-decoration: none;
    border-radius: 4px;
    font-weight: bold;
}

.login-btn:hover {
    background-color: #0056b3;
}
</style>
  