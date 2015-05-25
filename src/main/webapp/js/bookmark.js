function NewFavorite(){

if(window.external){
window.external.addFavorite(self.location, 'SPORTRAIT - Sportfotos online');
}
else{
window.alert("To bookmark my site press CTRL + D");
}
}
