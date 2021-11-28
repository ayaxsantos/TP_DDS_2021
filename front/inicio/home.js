const idUsuario = localStorage.getItem('idSesion');
const idAdmin = localStorage.getItem('idAdmin');
const idVol = localStorage.getItem('idVoluntario');
const tipoDocumento = localStorage.getItem('tipoDocumento');
const numerodocumento = localStorage.getItem('numeroDocumento');

if (idUsuario !== null || idAdmin !== null || idVol !== null) {
    document.getElementById("botonIniciarSesion").style.display = "none";
    document.getElementById("botonRegistrarse").style.display = "none";
    document.getElementById("elementoCerrarSesion").style.display = "block";
}

function redireccionamiento(input) {
    if (idUsuario !== null || numerodocumento !== null) {
        location.href = input
    } else {
        location.href = "../registrarPersona/registrarPersona.html"
    }
}

function cerrarSesion() {
    appCerrarSesionVue.cerrarSesion();
    localStorage.removeItem('idSesion');
    localStorage.removeItem('idAdmin');
    localStorage.removeItem('idVoluntario');
    location.href = "../inicio/home.html"
}

const appCerrarSesionVue = new Vue({
    methods: {
        cerrarSesion: function () {
            if (idUsuario !== null) {
                this.desconectar(idUsuario);
            }
            if (idAdmin !== null) {
                this.desconectar(idAdmin);
            }
            if (idVol !== null) {
                this.desconectar(idVol);
            }
        },
        desconectar: function (auth) {
            fetch('http://localhost:8080/usuarios/desconectar', {headers: {"Authorization": auth}}).then(response => {
                if (response.status !== 200) alert("Algo salio mal!!")
            })
        }
    }
});