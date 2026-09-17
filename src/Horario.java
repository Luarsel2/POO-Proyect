public class Horario {
    private int idHorario;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;

    public Horario(int idHorario, String diaSemana, String horaInicio, String horaFin){
        this.idHorario = idHorario;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    // Compara si este horario coincide en día con otro horario
    public boolean verificarChoque(Horario otroHorario) {
        if (otroHorario == null) return false;
        return this.diaSemana.equalsIgnoreCase(otroHorario.getDiaSemana()) && 
               this.horaInicio.equalsIgnoreCase(otroHorario.getHoraInicio());
    }

    // Retorna la duracion orientativa de la clase
    public int obtenerDuracion() {
        return 2; // Representación de 2 horas lectivas
    }

    // Getters y Setters
    public int getIdHorario() { 
        return idHorario; }
    public void setIdHorario(int idHorario) {
         this.idHorario = idHorario; }

    public String getDiaSemana() {
         return diaSemana; }
    public void setDiaSemana(String diaSemana) {
         this.diaSemana = diaSemana; }

    public String getHoraInicio() {
         return horaInicio; }
    public void setHoraInicio(String horaInicio) {
         this.horaInicio = horaInicio; }

    public String getHoraFin() {
         return horaFin; }
    public void setHoraFin(String horaFin) {
         this.horaFin = horaFin; }
}
