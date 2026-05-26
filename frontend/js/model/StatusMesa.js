/**
 * Enumeração que representa os estados de uma mesa no sistema Santini Gourmet.
 * Congelado com Object.freeze para garantir imutabilidade das chaves.
 */
const StatusMesa = Object.freeze({
    LIVRE: 'LIVRE',
    OCUPADA: 'OCUPADA',
    AGUARDANDO_LIMPEZA: 'AGUARDANDO_LIMPEZA'
});

export default StatusMesa;
