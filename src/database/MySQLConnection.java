package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.sql.ResultSetMetaData;

/**
 * <p>Um utilitário específico para o projeto que simplifica a conexão com bancos
 * de dados MySQL e disponibiliza algumas funções úteis.</p>
 * @author bolinhodourado
 */
public class MySQLConnection {
    
    /**
     * <p>O objeto de conexão com o banco de dados.</p>
     */
    private Connection connect;    
    
    /**
     * <p>Lista de Statement temporários. Seus objetos são removidos na chamada de
     * {@link MySQLConnection#close() close()}, que deve ser executado sempre
     * que uma query não for mais necessária.</p>
     */    
    private ArrayList<Statement> statements;
    
    /**
     * <p>Lista de ResultSet temporários. Seus objetos são removidos na chamada de
     * {@link MySQLConnection#close() close()}, que deve ser executado sempre
     * que uma query não for mais necessária.</p>
     */
    private ArrayList<ResultSet> resultSets;
    
    /**
     * <p>Inicializa uma conexão com um banco de dados MySQL.</p>
     * <p>Também inicializa as listas de objetos temporários.</p>
     * @param user o username do usuário no banco.
     * @param password a senha do usuário no banco.
     * @param database o nome do banco de dados a ser acessado.
     */
    public MySQLConnection(String user, String password, String database){
        
        statements = new ArrayList<>();
        resultSets = new ArrayList<>();
        
        try {
            connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost/" + database + "?useUnicode=yes"
                            + "&characterEncoding=UTF-8&user=" 
                            + user + "&password=" + password
            );
        } catch (SQLException ex) {
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * <p>Realiza operações de busca do do tipo {@code SELECT [colunas] FROM [tabela] 
     * [condições]}. Não realiza modificações. Para inserções, modificações e
     * exclusões, use {@link MySQLConnection#update(java.lang.String) 
     * update(String)}.</p>
     * <h3><strong>Exemplo:</strong></h3>
     * {@code SELECT id_cliente, id_fantasia FROM Aluguel WHERE data_insercao > '2023-02-02'}
     * <p>Seleciona os campos 'id_cliente' e 'id_fantasia' das linhas da tabela 
     * 'Aluguel' que foram inseridas depois de 02/02/2023.</p>
     * @param query a string de busca SQL.
     * @return um {@link ResultSet ResultSet} com o resultado da busca.
     */
    public ResultSet query(String query){
        System.out.println(query);
        try {
            Statement statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            statement.closeOnCompletion();
            statements.add(statement);
            resultSets.add(resultSet);
            resultSet.next();
            return resultSet;
        } catch (SQLException ex) {
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /**
     * <p>Realiza operações de inserção, modificação e exclusão com base em uma
     * linha SQL. Não realiza buscas. Para buscas, use 
     * {@link MySQLConnection#query(java.lang.String) query(String)}.</p>
     * <h3><strong>Exemplo:</strong></h3>
     * {@code INSERT INTO Categoria_Fantasia(id_fantasia, id_categoria) VALUES 
     * (1, 1), (1,2), (3, 7)}
     * <p>Insere 3 linhas na tabela Categoria_Fantasia com os campos 
     * 'id_fantasia' e 'id_categoria'</p>
     * @param query a linha SQL.
     * @return a quantidade de colunas com match pela linha SQL, 
     * e não a quantidade de colunas modificadas.
     */    
    public int update(String query){
        System.out.println(query);
        try (Statement statement = connect.createStatement()) {
            int matches = statement.executeUpdate(query);
            return matches;
        } catch (SQLException ex) {
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    
    /**
     * <p>Utilitário para imprimir pares (coluna, valor) a partir de consultas.</p>
     * @param result o resultado de uma consulta. Retorno de {@link MySQLConnection#query(java.lang.String) query()}.
     */
    public void printResult(ResultSet result){
        try {
            do {
                ResultSetMetaData meta = result.getMetaData();
                System.out.println("------------------------------------------");
                for(int i=1; i<=meta.getColumnCount(); i++){
                    System.out.println(meta.getColumnLabel(i) + ": " + result.getString(i));
                }
            } while (result.next());
        } catch (SQLException ex) {
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * <p>Finaliza os objetos temporários armazenados nas suas determinadas listas
     * e esvazia as mesmas.</P>
     */
    public void closeTemporaries(){
        try {
            for(ResultSet set: resultSets){
                set.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }        
        resultSets.clear();
        statements.clear();
    }
    
    // Específicos do banco
    
    /**
     * <p>Determina a quantidade de unidades da fantasia com identificador na
     * entrada com base no número de aluguéis com status 'ativo' relacionado
     * à fantasia em questão.</p>
     * @param idFantasia o identificador da fantasia no banco.
     * @return a quantidade de unidades disponíveis. Pode devolver um valor
     * negativo, o que indicaria inconsistência de dados no banco.
     */
    public int getQuantidadeDisponivel(int idFantasia){
        try (ResultSet qtdFantasia = 
                query("select quantidade from Fantasia where "
                        + "id_fantasia=" + idFantasia);
            ResultSet qtdAlugada = 
                query("select count(*) from Aluguel where "
                        + "status_aluguel='ativo' AND id_fantasia = " + idFantasia)) {            
            int qtdDisponivel = qtdFantasia.getInt(1) - qtdAlugada.getInt(1);
            qtdFantasia.close();
            qtdAlugada.close();
            return qtdDisponivel;
        } catch (SQLException ex) {
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    /**
     * <p>Determina a quantidade de unidades da fantasia com nome na
     * entrada com base no número de aluguéis com status 'ativo' relacionado
     * à fantasia em questão.</p>
     * @param nomeFantasia o nome da fantasia no banco.
     * @return a quantidade de unidades disponíveis. Pode devolver um valor
     * negativo, o que indicaria inconsistência de dados no banco.
     */
    public int getQuantidadeDisponivel(String nomeFantasia){
        try (ResultSet qtdFantasia = 
                query("select quantidade from Fantasia where "
                        + "nome_fantasia= '" + nomeFantasia + "'");
            ResultSet qtdAlugada = 
                query("select count(*) from Aluguel where "
                        + "status_aluguel='ativo' AND id_fantasia = " 
                        + query("select id_fantasia from Fantasia where "
                                + "nome_fantasia = '" + nomeFantasia + "'").
                                getInt("id_fantasia"))) {            
            int qtdDisponivel = qtdFantasia.getInt(1) - qtdAlugada.getInt(1);
            return qtdDisponivel;
        } catch (SQLException ex) {
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    
    /**
     * <p>Insere linhas com <strong>todos os atributos</strong> da tabela especificada.</p>
     * <p>Verifica se o admin em questão tem permissão de escrita.</p>
     * <h3><strong>Exemplo:</strong></h3>
     * {@code insert("Fantasia", 1, 
     *      "1, 'Fantasia Vampiro', 4, 'P', 122.30, 1, 2023-02-02",
     *      "2, 'Fantasia Vampiro', 1, 'G', 142.30, 1, 2023-02-02",
     *      "3, 'Fantasia Chapeuzinho Vermelho', 3, 'M', 240.00, 1, 2023-02-02";}
     * <p>Insere 3 linhas na tabela 'Fantasia'. Como a chave primária e a data
     * de inserção devem ser geradas automaticamente, não é recomendado.</p>
     * <p>Traduz para: 
     * <p>{@code INSERT INTO Fantasia VALUES 
     * (1, 'Fantasia Vampiro', 4, 'P', 122.30, 1, 2023-02-02),
     * (2, 'Fantasia Vampiro', 1, 'G', 142.30, 1, 2023-02-02),
     * (3, 'Fantasia Chapeuzinho Vermelho', 3, 'M', 240.00, 1, 2023-02-02)}.</p>
     * @param tabela o nome da tabela onde devem ser inseridos os dados.
     * @param idAdmin o identificador do admin a inserir os dados.
     * @param values os valores da primeira linha a serem inseridos. <strong>Linha
     * única</strong>. Valores adicionais devem ser colocados no parâmetro 'extraValues'.
     * @param extraValues linhas adicionais a serem inseridas.
     * @return se a inserção passou pela condição de permissão do admin a
     * inserir os dados. Erros advindos de condições no banco geram erros SQL.
     */
    public boolean insert(String tabela, int idAdmin, String values, 
            String... extraValues){
        try (var admin = query("SELECT allow_insert FROM DBAdmin WHERE id_admin = " + idAdmin)){
            if(!admin.getBoolean("allow_insert")){
                System.out.println("Erro de inserção: o admin especificado não "
                        + "tem permissão de escrita!");
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        String extra = "";
        for(String s: extraValues){
            extra = extra.concat(", (" + s + ")");
        }
        
        System.out.println("INSERT INTO " + tabela + " VALUES (" + values + ")" + extra + ";");
        
        update("INSERT INTO " + tabela + " VALUES (" + values + ")" + extra + ";");

        return true;
    }
    
    /**
     * <p>Insere linhas com os <strong>atributos especificados pela máscara</strong> 
     * na tabela especificada.</p>
     * <p>Verifica se o admin em questão tem permissão de escrita.</p>
     * <h3><strong>Exemplo:</strong></h3>
     * {@code insert("Fantasia", "nome_fantasia, quantidade, tamanho, 
     * valor_aluguel_dia, id_admin", 1, 
     *      "'Fantasia Vampiro', 4, 'P', 122.30, 1",
     *      "'Fantasia Vampiro', 1, 'G', 142.30, 1",
     *      "'Fantasia Chapeuzinho Vermelho', 3, 'M', 240.00, 1";}
     * <p>Insere 3 linhas na tabela 'Fantasia' com a máscara especificada. 
     * Nesse caso, a chave primária e a data de registro são preenchidas
     * automaticamente.</p>
     * <p>Traduz para: 
     * <p>{@code INSERT INTO Fantasia(nome_fantasia, quantidade, tamanho, 
     * valor_aluguel_dia, id_admin) VALUES 
     * ('Fantasia Vampiro', 4, 'P', 122.30, 1),
     * ('Fantasia Vampiro', 1, 'G', 142.30, 1),
     * ('Fantasia Chapeuzinho Vermelho', 3, 'M', 240.00, 1)}.</p>
     * @param tabela o nome da tabela onde devem ser inseridos os dados.
     * @param mask a máscara de atributos a serem inseridos. Especifica quais
     * colunas vão receber os valores de cada linha inserida. atributos não
     * especificadas na máscara recebem NULL. Atributos que não podem ser nulos
     * não especificados na máscara desencadeiram um erro SQL.
     * @param idAdmin o identificador do admin a inserir os dados.
     * @param values os valores da primeira linha a serem inseridos. <strong>Linha
     * única</strong>. Valores adicionais devem ser colocados no parâmetro 'extraValues'.
     * @param extraValues linhas adicionais a serem inseridas.
     * @return se a inserção passou pela condição de permissão do admin a
     * inserir os dados. Erros advindos de condições e triggers no banco geram 
     * erros SQL.
     */    
    public boolean insert(String tabela, String mask, int idAdmin, String values,
            String... extraValues){
        try (var admin = query("SELECT allow_insert FROM DBAdmin WHERE id_admin = " + idAdmin)){
            if(!admin.getBoolean("allow_insert")){
                System.out.println("Erro de inserção: o admin especificado não "
                        + "tem permissão de escrita!");
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        String extra = "";
        for(String s: extraValues){
            extra = extra.concat(", (" + s + ")");
        }

        System.out.println("INSERT INTO " + tabela + "(" + mask + ") VALUES "
                + "(" + values + ")" + extra + ";");
        
        update("INSERT INTO " + tabela + "(" + mask + ") VALUES "
                + "(" + values + ")" + extra + ";");

        return true;
    }
    
    /**
     * <p>Atualiza valores em uma tabela especificada baseados na máscara e na
     * condição de entrada.</p>
     * <p>Verifica se o admin em questão tem permissão de modificação.</p>
     * <h3><strong>Exemplo:</strong></h3>
     * {@code update("Fantasia", "nome_fantasia, tamanho", 1, "'Fantasia Papai 
     * Noel', 'G'", "id_fantasia<2");}
     * <p>Atualiza os campos 'nome_fantasia' e 'tamanho' das linhas com
     * identificador menor que 2 na tabela Fantasia.</p>
     * <p>Traduz para:</p>
     * {@code UPDATE Fantasia SET nome_fantasia='Fantasia Papai Noel', 
     * tamanho='G' WHERE id_fantasia<2;}
     * @param tabela o nome da tabela a ser modificada.
     * @param mask a máscara de atributos a serem modificados. Indica quais
     * atributos vão ser modificados na tabela.
     * @param idAdmin o identificador do admin a modificar os dados.
     * @param values os valores a serem modificados. Devem seguir a estrutura da
     * máscara.
     * @param condition a condição de modificação.
     * @return se a modificação passou pela condição de permissão do admin a 
     * modificar os dados. Erros advindos de condições e triggers no banco geram
     * erros SQL.
     */
    public boolean update(String tabela, String mask, int idAdmin, String values,
            String condition){
        try (var admin = query("SELECT allow_update FROM DBAdmin WHERE id_admin = " + idAdmin)){
            if(!admin.getBoolean("allow_update")){
                System.out.println("Erro de inserção: o admin especificado não "
                        + "tem permissão de escrita!");
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String assignmentString = "";
        String[] attributeList = mask.split(",");
        String[] valueList = values.split(",");
        
        for(int i=0; i<attributeList.length; i++){
            attributeList[i] = attributeList[i].trim();
            valueList[i] = valueList[i].trim();
            assignmentString = assignmentString.concat(attributeList[i] + " = " + valueList[i] + ", ");
        }
        if(!assignmentString.isEmpty()){
            assignmentString = assignmentString.substring(0, assignmentString.length()-2);
        }
        
        System.out.println("UPDATE " + tabela + " SET " + assignmentString
                + ((condition.isEmpty())?"":(" WHERE " + condition)));
        
        update("UPDATE " + tabela + " SET " + assignmentString
                + ((condition.isEmpty())?"":(" WHERE " + condition)));
        return true;
    }
    
    /**
     * <p>Remove linhas em uma tabela de acordo com a condição especificada.</p>
     * <p>Verifica se o admin em questão tem permissão de exclusão.</p>
     * <h3><strong>Exemplo:</strong></h3>
     * {@code delete("Aluguel", 1, "id_cliente=2");}
     * <p>Remove todas as linhas da tabela 'Aluguel' com o cliente de
     * identificador '2'.</p>
     * <p>Traduz para:</p>
     * {@code DELETE FROM Aluguel WHERE id_cliente=2}
     * @param tabela o nome da tabela a ter linhas excluídas.
     * @param idAdmin o identificador do admin a remover os dados.
     * @param condition a condição de exclusão.
     * @return se a exclusão passou pela condição de permissão do admin a 
     * excluir dados. Erros advindos de condições e triggers no banco geram
     * erros SQL.
     */
    public boolean delete(String tabela, int idAdmin, String condition){
        try (var admin = query("SELECT allow_delete FROM DBAdmin WHERE id_admin = " + idAdmin)){
            if(!admin.getBoolean("allow_delete")){
                System.out.println("Erro de exclusão: o admin especificado não "
                        + "tem permissão de escrita!");
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("DELETE FROM " + tabela + 
                ((condition.isEmpty())?"":(" WHERE " + condition)) + ";");
        
        update("DELETE FROM " + tabela + 
                ((condition.isEmpty())?"":(" WHERE " + condition)) + ";");
        return true;
    }
}
