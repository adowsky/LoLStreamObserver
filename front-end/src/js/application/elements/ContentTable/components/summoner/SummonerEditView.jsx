import React from 'react';
import ServerPicker from '../ServerPicker.jsx';
import ButtonGroup from '../ButtonGroup.jsx';

export default class SummonerEditView extends React.Component {
    constructor(props) {
        super(props);
        console.log("SEV got properties:" + props);
        this.keyPressedHandler = this.keyPressedHandler.bind(this);
    }

    keyPressedHandler(event){
        if(event.key == 'Enter'){
            this.props.handlers.editState();
        }
    }

    render() {
        let serverHandler = this.props.handlers.server;
        let nameHandler = this.props.handlers.name;
        let online = (this.props.online) ? " online" : "";
        return <div className="summoner-content">
                <ServerPicker value={this.props.server} handler={serverHandler}/>
                <input type="text" value={this.props.name} onChange={nameHandler} onKeyPress={this.keyPressedHandler}/>
            </div>
    }
}
SummonerEditView.propTypes = {
    handlers: React.PropTypes.object.isRequired,
    name: React.PropTypes.string.isRequired,
    server: React.PropTypes.string.isRequired
};