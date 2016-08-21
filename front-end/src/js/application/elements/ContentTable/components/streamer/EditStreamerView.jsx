import React from 'react';
import ButtonGroup from '../ButtonGroup.jsx';

export default class EditStreamerView extends React.Component {
    constructor(props) {
        super(props);
        this.keyPressedHandler = this.keyPressedHandler.bind(this);

    }

    keyPressedHandler(event){
        if(event.key == 'Enter'){
            this.props.handlers.editState();
        }
    }

    render() {
        let nameHandler = this.props.handlers.name;
        let name = this.props.name;
        let keyHandler = this.keyPressedHandler;
        return <div className="streamer-content">
                <input type="text" value={name} onChange={nameHandler} onKeyPress={keyHandler}/>
            </div>
    }
}
EditStreamerView.propTypes = {
    name: React.PropTypes.string.isRequired
};